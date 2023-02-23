package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.food.dto.PresetScheduleResponseDto;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
import co.dalicious.domain.food.mapper.PresetDailyFoodMapper;
import co.dalicious.domain.food.repository.*;
import co.dalicious.domain.recommend.dto.RecommendScheduleDto;
import co.dalicious.domain.recommend.entity.GroupRecommends;
import co.dalicious.domain.recommend.repository.QGroupRecommendRepository;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.schedules.ExcelPresetDailyFoodDto;
import co.kurrant.app.admin_api.dto.schedules.ExcelPresetDto;
import co.kurrant.app.admin_api.dto.schedules.ItemPageableResponseDto;
import co.kurrant.app.admin_api.dto.schedules.ScheduleResponseDto;
import co.kurrant.app.admin_api.mapper.ExcelPresetDailyFoodMapper;
import co.kurrant.app.admin_api.service.ScheduleService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
    private final QPresetDailyFoodRepository qPresetDailyFoodRepository;
    private final PresetDailyFoodMapper presetDailyFoodMapper;
    private final QGroupRecommendRepository qGroupRecommendRepository;
    private final QUserGroupRepository qUserGroupRepository;

    @Override
    @Transactional
    public void makePresetSchedulesByExcel(ExcelPresetDailyFoodDto dtoList) {
        LinkedList<ExcelPresetDailyFoodDto.ExcelData> dataList = new LinkedList<>(dtoList.getExcelDataList());

        // 서비스날, 식사 타입, 메이커스로 몪고. - makers
        MultiValueMap<ExcelPresetDto, ExcelPresetDailyFoodDto.ExcelData> makersGrouping = new LinkedMultiValueMap<>();
        for(ExcelPresetDailyFoodDto.ExcelData data : dataList) {
            ExcelPresetDto excelPresetDto = ExcelPresetDto.createExcelPresetDto(data);
            makersGrouping.add(excelPresetDto, data);
        }
        // 서비스날, 식사 타입, 메이커스, 그룹으로 묶고. - group
        MultiValueMap<ExcelPresetDto.ExcelGroupDataDto, ExcelPresetDailyFoodDto.ExcelData> groupGrouping = new LinkedMultiValueMap<>();
        for(ExcelPresetDailyFoodDto.ExcelData data : dataList) {
            ExcelPresetDto.ExcelGroupDataDto groupDataDto = ExcelPresetDto.ExcelGroupDataDto.createExcelGroupDto(data);
            groupGrouping.add(groupDataDto, data);
        }

        // 수정해야할 데이터인지 확인
        // 1. 마감시간이 지나지 않은 최근 생성되 데이터를 찾기
        List<PresetMakersDailyFood> existMakersDailyFoodList = qPresetMakersDailyFoodRepository.getMostRecentPresetsInDeadline();
        List<ExcelPresetDailyFoodDto.ExcelData> index = new ArrayList<>();
        // 2. 데이터가 있으면
        if(existMakersDailyFoodList != null) {
            // 3. 그룹핑한 데이터를 기준으로 메이커스를 만들거나 수정
            for(PresetMakersDailyFood makersDailyFood : existMakersDailyFoodList) {
                // 기존 내용을 가진 key가 있으면 해당 내용 수정
                ExcelPresetDto excelPresetDto = ExcelPresetDto.createExcelPresetDto(makersDailyFood); // 기존 것을 가지고 만든 키
                for(ExcelPresetDto presetDto : makersGrouping.keySet()) {
                    if(excelPresetDto.equals(presetDto)) { // 같은게 존재하면
                        // 값 중 처음 것을 가져와서 상태를 변경하고 for-loop 나가기
                        ExcelPresetDailyFoodDto.ExcelData firstMakersPreset =  Objects.requireNonNull(makersGrouping.get(presetDto)).stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
                        makersDailyFood.updatePresetMakersDailyFood(ScheduleStatus.ofCode(firstMakersPreset.getFoodScheduleStatus()), DateUtils.stringToLocalDateTime(dtoList.getDeadline()));
                        presetMakersDailyFoodRepository.save(makersDailyFood);
                        break;
                    }
                }
                // 4.preset daily food 상태 바꾸기
                List<PresetGroupDailyFood> groupDailyFoodList = makersDailyFood.getPresetGroupDailyFoods();
                for(PresetGroupDailyFood groupDailyFood : groupDailyFoodList) {
                    ExcelPresetDto.ExcelGroupDataDto excelGroupDataDto = ExcelPresetDto.ExcelGroupDataDto.createExcelGroupDto(makersDailyFood, groupDailyFood); // 기존 그룹이 가지고 있던 키
                    List<PresetDailyFood> dailyFoods = groupDailyFood.getPresetDailyFoods();
                    for(ExcelPresetDto.ExcelGroupDataDto groupDataDto : groupGrouping.keySet()) {
                        // 서비스 날, 식사 타입, 메이커스, 그룹이 동일한 키를 가지고 있으면
                        if(excelGroupDataDto.equals(groupDataDto)) {
                            //값을 가져와서
                            List<ExcelPresetDailyFoodDto.ExcelData> groupGroupingValueList = groupGrouping.get(groupDataDto);
                            if(groupGroupingValueList != null) {
                                for(PresetDailyFood dailyFood : dailyFoods) {
                                    //푸드의 schedule status 를 변경
                                    for(ExcelPresetDailyFoodDto.ExcelData value : groupGroupingValueList) {
                                        if(dailyFood.getFood().getName().equals(value.getFoodName())) {
                                            dailyFood.updateStatus(ScheduleStatus.ofCode(value.getFoodScheduleStatus()));
                                            presetDailyFoodRepository.save(dailyFood);
                                            index.add(value);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        //생성 - 이미 수정 혹은 생성되어서 index 에 들어간 excel data 를 제외하고 생성
        dataList.removeAll(index);
        if(dataList.size() != 0) {
            MultiValueMap<ExcelPresetDto, ExcelPresetDailyFoodDto.ExcelData> makersGroupingList = new LinkedMultiValueMap<>();
            for(ExcelPresetDailyFoodDto.ExcelData data : dataList) {
                ExcelPresetDto excelPresetDto = ExcelPresetDto.createExcelPresetDto(data);
                makersGroupingList.add(excelPresetDto, data);
            }
            // 서비스날, 식사 타입, 메이커스, 그룹으로 묶고. - group
            MultiValueMap<ExcelPresetDto.ExcelGroupDataDto, ExcelPresetDailyFoodDto.ExcelData> groupGroupingList = new LinkedMultiValueMap<>();
            for(ExcelPresetDailyFoodDto.ExcelData data : dataList) {
                ExcelPresetDto.ExcelGroupDataDto groupDataDto = ExcelPresetDto.ExcelGroupDataDto.createExcelGroupDto(data);
                groupGroupingList.add(groupDataDto, data);
            }

            // preset makers food daily
            List<PresetMakersDailyFood> presetMakersDailyFoods = new ArrayList<>();
            for(ExcelPresetDto presetDto : makersGroupingList.keySet()) {
                List<ExcelPresetDailyFoodDto.ExcelData> excelDataList = makersGroupingList.get(presetDto);
                if(excelDataList != null) {
                    Makers makers = makersRepository.findByName(presetDto.getMakersName());
                    // makers 가 없으면
                    if(makers == null) throw new ApiException(ExceptionEnum.NOT_FOUND_MAKERS);

                    ExcelPresetDailyFoodDto.ExcelData excelData = excelDataList.get(0);
                    PresetMakersDailyFood presetMakersDailyFood = excelPresetDailyFoodMapper.toMakersDailyFoodEntity(presetDto, excelData.getMakersScheduleStatus(), makers, dtoList.getDeadline());
                    presetMakersDailyFoodRepository.save(presetMakersDailyFood);
                    presetMakersDailyFoods.add(presetMakersDailyFood);
                }
            }

            // preset group food daily
            for(ExcelPresetDto.ExcelGroupDataDto groupDataDto : groupGroupingList.keySet()) {
                List<ExcelPresetDailyFoodDto.ExcelData> excelDataList = groupGroupingList.get(groupDataDto);
                Group group = groupRepository.findByName(groupDataDto.getGroupName());
                // group 이 없으면
                if(group == null) throw new ApiException(ExceptionEnum.GROUP_NOT_FOUND);

                // 앞에서 생성한 preset makers 의 서비스날, 메이커스, 식사 타입과 키가 가지고 있는 그룹의 내용이 동일한 preset makers 를 찾는다.
                PresetMakersDailyFood presetMakersDailyFood = null;
                for(PresetMakersDailyFood makersDailyFood : presetMakersDailyFoods) {
                    ExcelPresetDto.ExcelGroupDataDto excelGroupDataDto = ExcelPresetDto.ExcelGroupDataDto.createExcelGroupDto(makersDailyFood, group);
                    if(groupDataDto.equals(excelGroupDataDto)) {
                        presetMakersDailyFood = makersDailyFood;
                        break;
                    }
                }
                // excel data 중 가장 처음 걸로 preset group food daily 만들고
                if(excelDataList != null && presetMakersDailyFood != null) {
                    ExcelPresetDailyFoodDto.ExcelData excelData = excelDataList.get(0);
                    PresetGroupDailyFood presetGroupDailyFood = excelPresetDailyFoodMapper.toGroupDailyFoodEntity(excelData, group, presetMakersDailyFood);
                    presetGroupDailyFoodRepository.save(presetGroupDailyFood);

                    // preset food daily
                    for(ExcelPresetDailyFoodDto.ExcelData data : excelDataList) {
                        Food food = qFoodRepository.findByNameAndMakers(data.getFoodName(), presetMakersDailyFood.getMakers());
                        // food 가 없으면
                        if(food == null) throw new ApiException(ExceptionEnum.NOT_FOUND);

                        PresetDailyFood presetDailyFood = excelPresetDailyFoodMapper.toPresetDailyFoodEntity(data, food, presetGroupDailyFood);
                        presetDailyFoodRepository.save(presetDailyFood);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<ScheduleResponseDto> getAllPresetScheduleList(OffsetBasedPageRequest pageable, Integer size, Integer page) {
        // 필터링에서 검증 한 번 해주고
//        BigInteger groupId = !parameters.containsKey("group") || parameters.get("group").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("group")));
//        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("makersId")));
//
//        Group group = (groupId != null) ? groupRepository.findById(groupId)
//                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND)) : null;
//        Makers makers = (makersId != null) ? makersRepository.findById(makersId)
//                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS)) : null;

        Page<PresetDailyFood> allPresetScheduleList =  qPresetDailyFoodRepository.findAllByCreatedDate(pageable, size, page);
        if(allPresetScheduleList != null) {
//             food dto 만들기
            List<PresetScheduleResponseDto.foodSchedule> foodScheduleList =
                    allPresetScheduleList.get().map(presetDailyFoodMapper::toFoodScheduleDto).toList();

//             group dto 만들기

            // 1. preset group daily food 가져오기
            MultiValueMap<PresetGroupDailyFood, PresetScheduleResponseDto.foodSchedule> groupDailyFoodGroupingList = new LinkedMultiValueMap<>();
            for(PresetDailyFood presetDailyFood : allPresetScheduleList) {
                PresetGroupDailyFood groupDailyFood  = presetDailyFood.getPresetGroupDailyFood();

                // 2. 동일한 id 를 가지고 있는 food schedule dto를 찾아서 묶기
                PresetScheduleResponseDto.foodSchedule foodScheduleDto = null;
                for(PresetScheduleResponseDto.foodSchedule foodSchedule : foodScheduleList) {
                    if(foodSchedule.getPresetFoodId().equals(presetDailyFood.getId())) {
                        foodScheduleDto = foodSchedule;
                    }
                }
                groupDailyFoodGroupingList.add(groupDailyFood, foodScheduleDto);
            }

            // 3. 묶은 데이터로 group dto 만들기
            MultiValueMap<PresetMakersDailyFood, PresetScheduleResponseDto.clientSchedule> makersDailyFoodGroupingList = new LinkedMultiValueMap<>();
            for(PresetGroupDailyFood groupDailyFood: groupDailyFoodGroupingList.keySet()) {
                PresetScheduleResponseDto.clientSchedule clientSchedule = presetDailyFoodMapper.toClientScheduleDto(groupDailyFood, groupDailyFoodGroupingList.get(groupDailyFood));

//             makers dto 만들기

                // 1. makers daily food 별로 client schedule 묶기
                PresetMakersDailyFood makersDailyFood = groupDailyFood.getPresetMakersDailyFood();
                makersDailyFoodGroupingList.add(makersDailyFood, clientSchedule);
            }

            // 2. 묶은 데이터로 makers dto 만들기
            List<PresetScheduleResponseDto> presetScheduleResponseDtoList = new ArrayList<>();
            for(PresetMakersDailyFood makersDailyFood : makersDailyFoodGroupingList.keySet()) {
                PresetScheduleResponseDto responseDto = presetDailyFoodMapper.toDto(makersDailyFood, makersDailyFoodGroupingList.get(makersDailyFood));
                presetScheduleResponseDtoList.add(responseDto);
            }

            ScheduleResponseDto responseDtoList = getScheduleResponseDto(presetScheduleResponseDtoList);

            return ItemPageableResponseDto.<ScheduleResponseDto>builder().items(responseDtoList)
                    .total(allPresetScheduleList.getTotalElements()).count(allPresetScheduleList.getNumberOfElements())
                    .limit(pageable.getPageSize()).offset(pageable.getOffset()).build();

        }
        throw new ApiException(ExceptionEnum.NOT_FOUND);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<ScheduleResponseDto> getRecommendPresetSchedule(String startDate, OffsetBasedPageRequest pageable, Integer size, Integer page) {
        // start date 기준으로 2주 추천 테이블에서 데이터 가져오기
        Page<GroupRecommends> recommendsList = qGroupRecommendRepository.getRecommendPresetSchedule(pageable, size, page, startDate);

        MultiValueMap<RecommendScheduleDto, BigInteger> groupingByMakers = new LinkedMultiValueMap<>();
        if(recommendsList != null) {
            // 서비스 날, 다이닝 타입, 메이커스로 묶기
            for(GroupRecommends recommends : recommendsList) {
                RecommendScheduleDto dto = RecommendScheduleDto.createDto(recommends);
                groupingByMakers.add(dto, recommends.getGroupId());
            }
        } else throw new ApiException(ExceptionEnum.NOT_FOUND);

        // preset schedule response dto 만들기
        List<PresetScheduleResponseDto> scheduleResponseDtos = new ArrayList<>();
        for(RecommendScheduleDto recommendScheduleDto : groupingByMakers.keySet()) {
            // 메이커스 찾기
            Makers makers = makersRepository.findById(recommendScheduleDto.getMakersId()).orElseThrow(
                    () -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));
            // 메이커스 푸드 가져오기
            List<Food> foodList = qFoodRepository.findByMakers(makers);
            // 그룹 찾기
            List<BigInteger> groupIdList = groupingByMakers.get(recommendScheduleDto);
            // preset group schedule dto 과 preset food schedule dto 만들기
            List<PresetScheduleResponseDto.clientSchedule> clientSchedules = new ArrayList<>();
            if(groupIdList != null) {
                for(BigInteger groupId : groupIdList) {

                    //preset food schedule dto 만들기
                    List<PresetScheduleResponseDto.foodSchedule> foodScheduleList = new ArrayList<>();
                    for(Food food : foodList) {
                        PresetScheduleResponseDto.foodSchedule foodSchedule = presetDailyFoodMapper.recommendToFoodScheduleDto(food,recommendScheduleDto);
                        foodScheduleList.add(foodSchedule);
                    }

                    // group id로 group 찾길
                    Group group = groupRepository.findById(groupId).orElseThrow(
                            () -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

                    // 그룹에 속한 유저의 수 구하기
                    Integer groupCapacity = qUserGroupRepository.userCountInGroup(groupId);
                    // 스팟 중 가장 픽업 시간이 가장 빠른 시간 구해서 40분을 빼기 - 픽업 시간
                    List<Spot> spotList = group.getSpots();
                    LinkedList<LocalTime> deliveryTimes = new LinkedList<>();
                    for(Spot spot : spotList) {
                        deliveryTimes.add(spot.getDeliveryTime(recommendScheduleDto.getDiningType()));
                    }
                    LocalTime pickupTime = deliveryTimes.stream().min(LocalTime::compareTo).orElseThrow(
                            () -> new ApiException(ExceptionEnum.NOT_FOUND_MEAL_INFO)).minusMinutes(40);

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
                .total((long) recommendsList.getTotalPages()).count(recommendsList.getNumberOfElements())
                .limit((int) recommendsList.getTotalElements()).offset(pageable.getOffset()).build();
    }

    @Override
    @Transactional
    public void updateDataInTemporary(ExcelPresetDailyFoodDto dtoList) {
        //받아온 데이터에서 데이터만 추출
        List<ExcelPresetDailyFoodDto.ExcelData> excelData = dtoList.getExcelDataList();

        // 서비스날, 식사 타입, 메이커스로 몪고. - makers
        MultiValueMap<ExcelPresetDto, ExcelPresetDailyFoodDto.ExcelData> makersGrouping = new LinkedMultiValueMap<>();
        for(ExcelPresetDailyFoodDto.ExcelData data : excelData) {
            ExcelPresetDto excelPresetDto = ExcelPresetDto.createExcelPresetDto(data);
            makersGrouping.add(excelPresetDto, data);
        }
        // 서비스날, 식사 타입, 메이커스, 그룹으로 묶고. - group
        MultiValueMap<ExcelPresetDto.ExcelGroupDataDto, ExcelPresetDailyFoodDto.ExcelData> groupGrouping = new LinkedMultiValueMap<>();
        for(ExcelPresetDailyFoodDto.ExcelData data : excelData) {
            ExcelPresetDto.ExcelGroupDataDto groupDataDto = ExcelPresetDto.ExcelGroupDataDto.createExcelGroupDto(data);
            groupGrouping.add(groupDataDto, data);
        }


        //데이터가 이미 있는 데이터인지 한 번 보고 있으면 수정

        // 1. 오늘 이후에 있는 con의irm status 가 요청 상태가 아닌 데이터를 찾기
        List<PresetMakersDailyFood> existPresetMakersDailyFoodList = qPresetMakersDailyFoodRepository.findByServiceDateAndConfirmStatus();
        List<ExcelPresetDailyFoodDto.ExcelData> index = new ArrayList<>();
        // 2. 데이터가 있으면
        if(existPresetMakersDailyFoodList != null) {
            // 3. 그룹핑한 데이터를 기준으로 메이커스를 만들거나 수정
            for(PresetMakersDailyFood makersDailyFood : existPresetMakersDailyFoodList) {
                // 기존 내용을 가진 key가 있으면 해당 내용 수정
                ExcelPresetDto excelPresetDto = ExcelPresetDto.createExcelPresetDto(makersDailyFood); // 기존 것을 가지고 만든 키
                for(ExcelPresetDto presetDto : makersGrouping.keySet()) {
                    if(excelPresetDto.equals(presetDto)) { // 같은게 존재하면
                        // 값 중 처음 것을 가져와서 상태를 변경하고 for-loop 나가기
                        ExcelPresetDailyFoodDto.ExcelData firstMakersPreset =  Objects.requireNonNull(makersGrouping.get(presetDto)).stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
                        makersDailyFood.updatePresetMakersDailyFood(ScheduleStatus.ofCode(firstMakersPreset.getFoodScheduleStatus()), DateUtils.stringToLocalDateTime(dtoList.getDeadline()));
                        presetMakersDailyFoodRepository.save(makersDailyFood);
                        break;
                    }
                }
                // 4.preset daily food 상태 바꾸기
                List<PresetGroupDailyFood> groupDailyFoodList = makersDailyFood.getPresetGroupDailyFoods();
                for(PresetGroupDailyFood groupDailyFood : groupDailyFoodList) {
                    ExcelPresetDto.ExcelGroupDataDto excelGroupDataDto = ExcelPresetDto.ExcelGroupDataDto.createExcelGroupDto(makersDailyFood, groupDailyFood); // 기존 그룹이 가지고 있던 키
                    List<PresetDailyFood> dailyFoods = groupDailyFood.getPresetDailyFoods();
                    for(ExcelPresetDto.ExcelGroupDataDto groupDataDto : groupGrouping.keySet()) {
                        // 서비스 날, 식사 타입, 메이커스, 그룹이 동일한 키를 가지고 있으면
                        if(excelGroupDataDto.equals(groupDataDto)) {
                            //값을 가져와서
                            List<ExcelPresetDailyFoodDto.ExcelData> groupGroupingValueList = groupGrouping.get(groupDataDto);
                            if(groupGroupingValueList != null) {
                                for(PresetDailyFood dailyFood : dailyFoods) {
                                    //푸드의 schedule status 를 변경
                                    for(ExcelPresetDailyFoodDto.ExcelData value : groupGroupingValueList) {
                                        if(dailyFood.getFood().getName().equals(value.getFoodName())) {
                                            dailyFood.updateStatus(ScheduleStatus.ofCode(value.getFoodScheduleStatus()));
                                            presetDailyFoodRepository.save(dailyFood);
                                            index.add(value);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        //없으면 생성해서 confirm status 를 pause 로 바꾼다.

    }

    private ScheduleResponseDto getScheduleResponseDto(List<PresetScheduleResponseDto> presetScheduleResponseDtoList) {
        // 드롭박스에 들어갈 데이터
        List<Group> group = groupRepository.findAll();
        List<Makers> makers = makersRepository.findAll();
        // 묶어서 schedule responseDto 만들기
        ScheduleResponseDto responseDtoList = ScheduleResponseDto.createdResponseDto(group, makers, presetScheduleResponseDtoList);
        return responseDtoList;
    }
}
