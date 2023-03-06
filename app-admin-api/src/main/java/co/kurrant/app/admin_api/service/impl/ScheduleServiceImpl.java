package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.food.dto.PresetScheduleResponseDto;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.ConfirmStatus;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
import co.dalicious.domain.food.mapper.PresetDailyFoodMapper;
import co.dalicious.domain.food.repository.*;
import co.dalicious.domain.order.dto.CapacityDto;
import co.dalicious.domain.recommend.dto.RecommendScheduleDto;
import co.dalicious.domain.recommend.entity.GroupRecommends;
import co.dalicious.domain.recommend.repository.QGroupRecommendRepository;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.dto.schedules.ExcelPresetDailyFoodDto;
import co.kurrant.app.admin_api.dto.schedules.ExcelPresetDto;
import co.kurrant.app.admin_api.dto.schedules.ScheduleResponseDto;
import co.kurrant.app.admin_api.mapper.ExcelPresetDailyFoodMapper;
import co.kurrant.app.admin_api.service.ScheduleService;
import exception.ApiException;
import exception.ExceptionEnum;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigInteger;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final MakersRepository makersRepository;
    private final ExcelPresetDailyFoodMapper excelPresetDailyFoodMapper;
    private final PresetMakersDailyFoodRepository presetMakersDailyFoodRepository;
    private final QPresetMakersDailyFoodRepository qPresetMakersDailyFoodRepository;
    private final PresetGroupDailyFoodRepository presetGroupDailyFoodRepository;
    private final PresetDailyFoodRepository presetDailyFoodRepository;
    private final GroupRepository groupRepository;
    private final QFoodRepository qFoodRepository;
    private final PresetDailyFoodMapper presetDailyFoodMapper;
    private final QGroupRecommendRepository qGroupRecommendRepository;
    private final QUserGroupRepository qUserGroupRepository;
    private final QMakersRepository qMakersRepository;
    private final QMakersCapacityRepository qMakersCapacityRepository;
    private final QGroupRepository qGroupRepository;
    private final QPresetGroupDailyFoodRepository qPresetGroupDailyFoodRepository;
    private final QPresetDailyFoodRepository qPresetDailyFoodRepository;

    @Override
    @Transactional
    public void makePresetSchedulesByExcel(ExcelPresetDailyFoodDto dtoList) {
        LinkedList<ExcelPresetDailyFoodDto.ExcelData> dataList = new LinkedList<>(dtoList.getExcelDataList());

        // 서비스날, 식사 타입, 메이커스로 몪고. - makers
        Set<String> makersNames = new HashSet<>();
        MultiValueMap<ExcelPresetDto, ExcelPresetDailyFoodDto.ExcelData> makersGrouping = new LinkedMultiValueMap<>();
        goupingByMakersAndServiceDateAndDiningType(dataList, makersGrouping, makersNames);
        List<Makers> makersList = qMakersRepository.getMakersByName(makersNames);
        Map<Makers, List<MakersCapacity>> makersCapaList = new HashMap<>();
        for(Makers makers : makersList) {
            List<MakersCapacity> makersCapacities = qMakersCapacityRepository.findByMakers(makers);
            makersCapaList.put(makers, makersCapacities);
        }
        // 서비스날, 식사 타입, 메이커스, 그룹으로 묶고. - group
        Set<String> groupNames = new HashSet<>();
        Set<String> foodNames = new HashSet<>();
        MultiValueMap<ExcelPresetDto.ExcelGroupDataDto, ExcelPresetDailyFoodDto.ExcelData> groupGrouping = new LinkedMultiValueMap<>();
        groupingByGroupAndMakersAndServiceDateAndDiningType(dataList, groupGrouping, groupNames, foodNames);
        List<Group> groupList = qGroupRepository.findAllByNames(groupNames);
        List<Food> foodList = qFoodRepository.findByNames(foodNames);

        // 현재 있는 데이터를 찾아오기
        List<PresetMakersDailyFood> existMakersDailyFoodList = qPresetMakersDailyFoodRepository.findByServiceDateAndConfirmStatus();
        Map<ExcelPresetDto, PresetMakersDailyFood> existPresetDataList = groupingExistPresetMakersDailyFoodData(existMakersDailyFoodList);

        for(ExcelPresetDto presetDto : makersGrouping.keySet()) {
            // 동일한 메이커스, 서비스 날, 식사타입을 가진 실제 데이터가 있다면 수정
            List<ExcelPresetDailyFoodDto.ExcelData> updateMakersDataList = makersGrouping.get(presetDto);
            ExcelPresetDailyFoodDto.ExcelData makersUpdateData = Objects.requireNonNull(updateMakersDataList).get(0);

            Makers makers = null;
            for(Makers match : makersList) {
                if(match.getName().equalsIgnoreCase(presetDto.getMakersName())) makers = match;
            }
            if(makers == null) throw new ApiException(ExceptionEnum.NOT_FOUND_MAKERS);

            List<MakersCapacity> makersCapacities = null;
            for(Makers match : makersCapaList.keySet()) {
                if(match.equals(makers)) {
                    makersCapacities = makersCapaList.get(match);
                }
            }

            if(existPresetDataList.containsKey(presetDto)) {
                PresetMakersDailyFood existMakersPreset = existPresetDataList.get(presetDto);
                existMakersPreset.updatePresetMakersDailyFood(ScheduleStatus.ofCode(makersUpdateData.getMakersScheduleStatus()), DateUtils.stringToLocalDateTime(dtoList.getDeadline()), ConfirmStatus.REQUEST);
                presetMakersDailyFoodRepository.save(existMakersPreset);

                // group preset daily food
                List<PresetGroupDailyFood> existGroupPreset = existMakersPreset.getPresetGroupDailyFoods();
                Map<ExcelPresetDto.ExcelGroupDataDto, PresetGroupDailyFood> existGroupPresetDataList = new HashMap<>();
                for(PresetGroupDailyFood presetGroupDailyFood : existGroupPreset) {
                    existGroupPresetDataList.put(ExcelPresetDto.ExcelGroupDataDto.createExcelGroupDto(existMakersPreset, presetGroupDailyFood), presetGroupDailyFood);
                }

                // 수정할 데이터인지 확인
                for(ExcelPresetDto.ExcelGroupDataDto groupPresetDto : groupGrouping.keySet()) {
                    List<ExcelPresetDailyFoodDto.ExcelData> updatePresetDataList = groupGrouping.get(groupPresetDto);

                    // 수정할 데이터이면 푸드 스테이터스 변경
                    if(existGroupPresetDataList.containsKey(groupPresetDto)) {
                        List<PresetDailyFood> existPresetDailyFood = existGroupPresetDataList.get(groupPresetDto).getPresetDailyFoods();
                        for(PresetDailyFood presetDailyFood : existPresetDailyFood) {
                            for(ExcelPresetDailyFoodDto.ExcelData updateData : Objects.requireNonNull(updatePresetDataList)) {
                                if(updateData.getFoodName().equals(presetDailyFood.getFood().getName())) {
                                    presetDailyFood.updateStatus(ScheduleStatus.ofCode(updateData.getFoodScheduleStatus()));
                                    presetDailyFoodRepository.save(presetDailyFood);
                                }
                            }
                        }
                    }
                    // 생성해야 할 데이터면 생성
                    else {
                        if(groupPresetDto.getServiceDate().equals(presetDto.getServiceDate()) &&
                                groupPresetDto.getDiningType().equals(presetDto.getDiningType()) &&
                                groupPresetDto.getMakersName().equals(presetDto.getMakersName())) {
                            createPresetGroupDailyFood(existMakersPreset, groupPresetDto, updatePresetDataList, groupList, foodList);
                        }
                    }
                }
            }
            // 동일한 메이커스, 서비스 날, 식사 타입을 가진 실제 데이터가 없다면 생성
            else {
                PresetMakersDailyFood newPresetMakersDailyFood = excelPresetDailyFoodMapper.toMakersDailyFoodEntity(presetDto, makersUpdateData.getMakersScheduleStatus(), makers, dtoList.getDeadline(), ConfirmStatus.REQUEST, makersCapacities);
                presetMakersDailyFoodRepository.save(newPresetMakersDailyFood);

                // preset group daily food 와 preset daily food 만들기
                for(ExcelPresetDto.ExcelGroupDataDto groupPresetDto : groupGrouping.keySet()) {
                    List<ExcelPresetDailyFoodDto.ExcelData> createPresetDataList = groupGrouping.get(groupPresetDto);
                    if(groupPresetDto.getServiceDate().equals(presetDto.getServiceDate()) &&
                            groupPresetDto.getDiningType().equals(presetDto.getDiningType()) &&
                    groupPresetDto.getMakersName().equals(presetDto.getMakersName())) {
                        createPresetGroupDailyFood(newPresetMakersDailyFood, groupPresetDto, createPresetDataList, groupList, foodList);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<ScheduleResponseDto> getAllPresetScheduleList(Map<String, Object> parameters, OffsetBasedPageRequest pageable, Integer size, Integer page) {
        // 필터링에서 검증 한 번 해주고
        List<BigInteger> groupIdList = !parameters.containsKey("groupId") || parameters.get("groupId").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("groupId"));
        List<BigInteger> makerIdList = !parameters.containsKey("makersId") || parameters.get("makersId").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("makersId"));
        List<Integer> scheduleStatusList = !parameters.containsKey("status") || parameters.get("status").equals("") ? null : StringUtils.parseIntegerList((String) parameters.get("status"));

        // PresetMakersDailyFood 페이징 조회
        Page<PresetMakersDailyFood> allPresetSchedulesPage = qPresetMakersDailyFoodRepository.findAllServiceDateAndConfirmStatusAndFilter(makerIdList, scheduleStatusList, pageable, size, page);

        // PresetMakersDailyFood가 없는 경우 예외 처리
        List<PresetScheduleResponseDto> presetScheduleResponseDtoList = Optional.ofNullable(allPresetSchedulesPage)
                .map(preset -> preset.stream()
                        .map(makersPreset -> {
                            List<PresetGroupDailyFood> groupDailyFoods = makersPreset.getPresetGroupDailyFoods();
                            List<PresetScheduleResponseDto.clientSchedule> clientSchedules = new ArrayList<>();

                            groupDailyFoods.forEach(groupPreset -> {
                                // groupIds 파라미터가 있을 경우 해당 그룹의 PresetDailyFood만 가져옴
                                if (groupIdList == null || groupIdList.contains(groupPreset.getGroup().getId())) {
                                    List<PresetDailyFood> presetDailyFoods = groupPreset.getPresetDailyFoods();
                                    List<PresetScheduleResponseDto.foodSchedule> foodSchedules = presetDailyFoods.stream()
                                            .map(presetDailyFoodMapper::toFoodScheduleDto)
                                            .collect(Collectors.toList());
                                    PresetScheduleResponseDto.clientSchedule clientSchedule = presetDailyFoodMapper.toClientScheduleDto(groupPreset, foodSchedules);
                                    clientSchedules.add(clientSchedule);
                                }
                            });
                            PresetScheduleResponseDto responseDto = presetDailyFoodMapper.toDto(makersPreset, clientSchedules);
                            return responseDto;
                        })
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

        ScheduleResponseDto responseDtoList = getScheduleResponseDto(presetScheduleResponseDtoList);

        return ItemPageableResponseDto.<ScheduleResponseDto>builder().items(responseDtoList).total(allPresetSchedulesPage.getTotalPages())
                .count(allPresetSchedulesPage.getNumberOfElements()).limit(pageable.getPageSize()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<ScheduleResponseDto> getRecommendPresetSchedule(String startDate, String endDate, OffsetBasedPageRequest pageable, Integer size, Integer page) {
        // start date 기준으로 2주 추천 테이블에서 데이터 가져오기
        Page<GroupRecommends> recommendsList = qGroupRecommendRepository.getRecommendPresetSchedule(pageable, size, page, startDate, endDate);
        Set<BigInteger> makersIdList = new HashSet<>();
        Set<BigInteger> groupIdList = new HashSet<>();
        for(GroupRecommends recommends : recommendsList) {
            makersIdList.add(recommends.getMakersId());
            groupIdList.add(recommends.getGroupId());
        }
        List<Makers> makersList = qMakersRepository.findMakersListById(makersIdList);
        List<Group> groupsList = qGroupRepository.findAllByIds(groupIdList);

        MultiValueMap<RecommendScheduleDto, BigInteger> groupingByMakers = new LinkedMultiValueMap<>();
        // 서비스 날, 다이닝 타입, 메이커스로 묶기
        for(GroupRecommends recommends : recommendsList) {
            RecommendScheduleDto dto = RecommendScheduleDto.createDto(recommends);
            groupingByMakers.add(dto, recommends.getGroupId());
        }

        // preset schedule response dto 만들기
        List<PresetScheduleResponseDto> scheduleResponseDtos = new ArrayList<>();
        for(RecommendScheduleDto recommendScheduleDto : groupingByMakers.keySet()) {
            // 메이커스 찾기
            Makers makers = makersList.stream().filter(match -> match.getId().equals(recommendScheduleDto.getMakersId())).findFirst().orElseThrow(
                    () -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS)
            );
            // 메이커스 푸드 가져오기
            List<Food> foodList = qFoodRepository.findByMakersAndFoodStatus(makers);
            // 그룹 찾기
            List<BigInteger> groupingGroupIdList = groupingByMakers.get(recommendScheduleDto);
            // preset group schedule dto 과 preset food schedule dto 만들기
            List<PresetScheduleResponseDto.clientSchedule> clientSchedules = new ArrayList<>();
            if(groupingGroupIdList != null) {
                for(BigInteger groupId : groupingGroupIdList) {

                    //preset food schedule dto 만들기
                    List<PresetScheduleResponseDto.foodSchedule> foodScheduleList = new ArrayList<>();
                    for(Food food : foodList) {
                        PresetScheduleResponseDto.foodSchedule foodSchedule = presetDailyFoodMapper.recommendToFoodScheduleDto(food,recommendScheduleDto);
                        foodScheduleList.add(foodSchedule);
                    }

                    // group id로 group 찾길
                    Group group = groupsList.stream().filter(match -> match.getId().equals(groupId)).findFirst().orElseThrow(
                            () -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND)
                    );
                    // 그룹에 속한 유저의 수 구하기
                    Integer groupCapacity = qUserGroupRepository.userCountInGroup(groupId);
                    // 스팟 중 가장 픽업 시간이 가장 빠른 시간 구해서 40분을 빼기 - 픽업 시간
                    List<Spot> spotList = group.getSpots();
                    LinkedList<LocalTime> deliveryTimes = new LinkedList<>();
                    for(Spot spot : spotList) {
                        List<MealInfo> mealInfoList = spot.getMealInfos();
                        if(mealInfoList.size() == 0) {
                            if(recommendScheduleDto.getDiningType().equals(DiningType.MORNING)) deliveryTimes.add(DateUtils.stringToLocalTime("07:00"));
                            else if(recommendScheduleDto.getDiningType().equals(DiningType.LUNCH)) deliveryTimes.add(DateUtils.stringToLocalTime("12:00"));
                            else if(recommendScheduleDto.getDiningType().equals(DiningType.DINNER)) deliveryTimes.add(DateUtils.stringToLocalTime("18:00"));
                        } else deliveryTimes.add(spot.getDeliveryTime(recommendScheduleDto.getDiningType()));
                    }
                    String pickupTime = DateTimeFormatter.ofPattern("HH:mm").format(deliveryTimes.stream().min(LocalTime::compareTo).orElseThrow(
                            () -> new ApiException(ExceptionEnum.NOT_FOUND_MEAL_INFO)).minusMinutes(40));


                    // preset group schedule dto 만들기
                    PresetScheduleResponseDto.clientSchedule clientSchedule = presetDailyFoodMapper.recommendToClientScheduleDto(group, groupCapacity, pickupTime,foodScheduleList);
                    clientSchedules.add(clientSchedule);
                }
            }

            // preset makers schedule
            PresetScheduleResponseDto responseDto = presetDailyFoodMapper.recommendToDto(makers, recommendScheduleDto, clientSchedules);
            scheduleResponseDtos.add(responseDto);
        }

        ScheduleResponseDto responseDtoList = getScheduleResponseDto(scheduleResponseDtos);

        return ItemPageableResponseDto.<ScheduleResponseDto>builder().items(responseDtoList)
                .total(recommendsList.getTotalPages()).count(recommendsList.getNumberOfElements())
                .limit(recommendsList.getTotalPages()).build();
    }

    @Override
    @Transactional
    public void updateDataInTemporary(ExcelPresetDailyFoodDto dtoList) {
        LinkedList<ExcelPresetDailyFoodDto.ExcelData> dataList = new LinkedList<>(dtoList.getExcelDataList());

        Set<String> makersNames = new HashSet<>();
        Set<String> groupNames = new HashSet<>();
        MultiValueMap<ExcelPresetDto, ExcelPresetDailyFoodDto.ExcelData> makersGroupingList = new LinkedMultiValueMap<>();
        MultiValueMap<ExcelPresetDto.ExcelGroupDataDto, ExcelPresetDailyFoodDto.ExcelData> groupGroupingList = new LinkedMultiValueMap<>();
        for(ExcelPresetDailyFoodDto.ExcelData data : dataList) {
            ExcelPresetDto presetDto = ExcelPresetDto.createExcelPresetDto(data);
            ExcelPresetDto.ExcelGroupDataDto groupPresetDto = ExcelPresetDto.ExcelGroupDataDto.createExcelGroupDto(data);
            makersGroupingList.add(presetDto, data);
            groupGroupingList.add(groupPresetDto, data);
            makersNames.add(data.getMakersName());
            groupNames.add(data.getGroupName());
        }

        List<Makers> makersList = qMakersRepository.getMakersByName(makersNames);
        List<Group> groupList = qGroupRepository.findAllByNames(groupNames);

        Map<Group, Integer> groupCapacityList = qUserGroupRepository.userCountsInGroup(groupList);

        Map<Makers, List<MakersCapacity>> makersCapacityGroupingByMakers = new HashMap<>();
        Map<Makers, List<Food>> foodListGroupingByMakers = new HashMap<>();
        List<Food> foodListByMakers = qFoodRepository.findByMakers(makersList);
        for(Makers makers : makersList) {
            List<Food> foodList = foodListByMakers.stream().filter(food -> food.getMakers().equals(makers)).toList();
            foodListGroupingByMakers.put(makers, foodList);

            List<MakersCapacity> makersCapacityList = makers.getMakersCapacities();
            makersCapacityGroupingByMakers.put(makers, makersCapacityList);
        }

        List<PresetMakersDailyFood> existPresetMakersDailyFood = qPresetMakersDailyFoodRepository.findByServiceDateAndConfirmStatus();
        List<PresetGroupDailyFood> existPresetGroupDailyFood = qPresetGroupDailyFoodRepository.findAllAndPresetMakersDailyFood(existPresetMakersDailyFood);
        List<PresetDailyFood> existPresetDailyFood = qPresetDailyFoodRepository.getAllAndPresetGroupDailyFood(existPresetGroupDailyFood);
        presetMakersDailyFoodRepository.deleteAll(existPresetMakersDailyFood);
        presetGroupDailyFoodRepository.deleteAll(existPresetGroupDailyFood);
        presetDailyFoodRepository.deleteAll(existPresetDailyFood);

        List<PresetMakersDailyFood> presetMakersDailyFoodList = new ArrayList<>();
        List<PresetGroupDailyFood> presetGroupDailyFoodList = new ArrayList<>();
        List<PresetDailyFood> presetDailyFoodList = new ArrayList<>();

        for(ExcelPresetDto presetDto : makersGroupingList.keySet()) {
            List<ExcelPresetDailyFoodDto.ExcelData> updateMakersDataList = makersGroupingList.get(presetDto);
            ExcelPresetDailyFoodDto.ExcelData makersUpdateData = Objects.requireNonNull(updateMakersDataList).stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

            Makers makers = makersList.stream().filter(match -> match.getName().equalsIgnoreCase(presetDto.getMakersName())).findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));
            List<MakersCapacity> makersCapacities = makersCapacityGroupingByMakers.keySet().stream()
                    .filter(match -> match.equals(makers)).findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS_CAPACITY)).getMakersCapacities();

            PresetMakersDailyFood presetMakersDailyFood = excelPresetDailyFoodMapper.toMakersDailyFoodEntity(presetDto, makersUpdateData.getFoodScheduleStatus(), makers, dtoList.getDeadline(), ConfirmStatus.PAUSE, makersCapacities);
            presetMakersDailyFoodList.add(presetMakersDailyFood);

            for(ExcelPresetDto.ExcelGroupDataDto groupDataDto : groupGroupingList.keySet()) {
                List<ExcelPresetDailyFoodDto.ExcelData> createGroupDataList = groupGroupingList.get(groupDataDto);
                ExcelPresetDailyFoodDto.ExcelData groupUpdateData = Objects.requireNonNull(createGroupDataList).stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

                Group group = groupList.stream().filter(match -> match.getName().equalsIgnoreCase(groupDataDto.getGroupName())).findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

                List<DiningType> groupDiningType = group.getDiningTypes();
                DiningType match = groupDiningType.stream().filter(diningType -> diningType.equals(groupDataDto.getDiningType())).findFirst().orElse(null);
                if(match != null) {
                    PresetGroupDailyFood presetGroupDailyFood = excelPresetDailyFoodMapper.toGroupDailyFoodEntity(groupUpdateData, group, presetMakersDailyFood);
                    presetGroupDailyFoodList.add(presetGroupDailyFood);
                    for(ExcelPresetDailyFoodDto.ExcelData updateDailyFoodData : createGroupDataList) {
                        Food food = foodListGroupingByMakers.get(makers).stream().filter(matchFood -> matchFood.getName().equalsIgnoreCase(updateDailyFoodData.getFoodName())).findFirst().orElse(null);
                        if (food != null) {
                            PresetDailyFood presetDailyFood = excelPresetDailyFoodMapper.toPresetDailyFoodEntity(updateDailyFoodData, food, presetGroupDailyFood);
                            presetDailyFoodList.add(presetDailyFood);
                        }
                    }
                }
            }
        }

        presetMakersDailyFoodRepository.saveAll(presetMakersDailyFoodList);
        presetGroupDailyFoodRepository.saveAll(presetGroupDailyFoodList);
        presetDailyFoodRepository.saveAll(presetDailyFoodList);
    }

    private ScheduleResponseDto getScheduleResponseDto(List<PresetScheduleResponseDto> presetScheduleResponseDtoList) {
        // 드롭박스에 들어갈 데이터
        List<Group> group = groupRepository.findAll();
        List<Makers> makers = makersRepository.findAll();
        // 묶어서 schedule responseDto 만들기
        return ScheduleResponseDto.createdResponseDto(group, makers, presetScheduleResponseDtoList);
    }

    private void createPresetGroupDailyFood(PresetMakersDailyFood presetMakersDailyFood, ExcelPresetDto.ExcelGroupDataDto groupPresetDto, List<ExcelPresetDailyFoodDto.ExcelData> presetDataList, List<Group> groupList, List<Food> foodList) {
        ExcelPresetDailyFoodDto.ExcelData createGroupPresetData = Objects.requireNonNull(presetDataList).get(0);
        Group group = groupList.stream().filter(match -> match.getName().equalsIgnoreCase(groupPresetDto.getGroupName())).findFirst().orElse(null);
        if (group == null) throw new ApiException(ExceptionEnum.GROUP_NOT_FOUND);

        DiningType match = group.getDiningTypes().stream().filter(diningType -> diningType.equals(groupPresetDto.getDiningType())).findFirst().orElse(null);
        if (match != null) {
            PresetGroupDailyFood newPresetGroupDailyFood = excelPresetDailyFoodMapper.toGroupDailyFoodEntity(createGroupPresetData, group, presetMakersDailyFood);
            presetGroupDailyFoodRepository.save(newPresetGroupDailyFood);
            for (ExcelPresetDailyFoodDto.ExcelData createData : presetDataList) {
                Food food = foodList.stream().filter(foodMatch -> foodMatch.getName().equalsIgnoreCase(createData.getFoodName())).findFirst().orElse(null);
                if (food == null) throw new ApiException(ExceptionEnum.NOT_FOUND);
                PresetDailyFood newPresetDailyFood = excelPresetDailyFoodMapper.toPresetDailyFoodEntity(createData, food, newPresetGroupDailyFood);
                presetDailyFoodRepository.save(newPresetDailyFood);
            }
        }
    }

    private Map<ExcelPresetDto, PresetMakersDailyFood> groupingExistPresetMakersDailyFoodData(List<PresetMakersDailyFood> existMakersDailyFoodList) {
        Map<ExcelPresetDto, PresetMakersDailyFood> existPresetDataList = new HashMap<>();
        for(PresetMakersDailyFood presetMakersDailyFood : existMakersDailyFoodList) {
            existPresetDataList.put(ExcelPresetDto.createExcelPresetDto(presetMakersDailyFood), presetMakersDailyFood);
        }
        return existPresetDataList;
    }

    private void groupingByGroupAndMakersAndServiceDateAndDiningType(LinkedList<ExcelPresetDailyFoodDto.ExcelData> dataList, MultiValueMap<ExcelPresetDto.ExcelGroupDataDto, ExcelPresetDailyFoodDto.ExcelData> groupGrouping, Set<String> groupNames, Set<String> foodNames) {
        for(ExcelPresetDailyFoodDto.ExcelData data : dataList) {
            ExcelPresetDto.ExcelGroupDataDto groupDataDto = ExcelPresetDto.ExcelGroupDataDto.createExcelGroupDto(data);
            groupGrouping.add(groupDataDto, data);
            groupNames.add(data.getGroupName());
            foodNames.add(data.getFoodName());
        }
    }

    private void goupingByMakersAndServiceDateAndDiningType(LinkedList<ExcelPresetDailyFoodDto.ExcelData> dataList, MultiValueMap<ExcelPresetDto, ExcelPresetDailyFoodDto.ExcelData> makersGrouping, Set<String> makersNames) {
        for(ExcelPresetDailyFoodDto.ExcelData data : dataList) {
            ExcelPresetDto excelPresetDto = ExcelPresetDto.createExcelPresetDto(data);
            makersGrouping.add(excelPresetDto, data);
            makersNames.add(data.getMakersName());
        }
    }
}
