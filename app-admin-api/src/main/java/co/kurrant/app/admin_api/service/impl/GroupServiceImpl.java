package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.address.repository.QRegionRepository;
import co.dalicious.domain.address.utils.AddressUtil;
import co.dalicious.domain.application_form.dto.mySpotZone.AdminListResponseDto;
import co.dalicious.domain.application_form.dto.mySpotZone.CreateRequestDto;
import co.dalicious.domain.application_form.dto.mySpotZone.UpdateRequestDto;
import co.dalicious.domain.application_form.dto.mySpotZone.UpdateStatusDto;
import co.dalicious.domain.application_form.mapper.MySpotZoneMapper;
import co.dalicious.domain.client.dto.FilterInfo;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.dto.filter.FilterDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.domain.client.mapper.MySpotZoneMealInfoMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.integration.client.user.entity.Region;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.DiningTypesUtils;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.service.GroupService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    public final QCorporationRepository qCorporationRepository;
    public final QUserRepository qUserRepository;
    public final GroupMapper groupMapper;
    public final QGroupRepository qGroupRepository;
    public final GroupRepository groupRepository;
    private final MealInfoRepository mealInfoRepository;
    private final UserRepository userRepository;
    private final MySpotZoneMapper mySpotZoneMapper;
    private final QMySpotZoneRepository qMySpotZoneRepository;
    private final QRegionRepository qRegionRepository;
    private final MySpotZoneMealInfoMapper mySpotZoneMealInfoMapper;

    @Override
    @Transactional
    public List<GroupDto.Spot> getSpots(BigInteger groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
        List<Spot> spots = group.getSpots();
        return groupMapper.spotsToDtos(spots);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<GroupListDto> getGroupList(BigInteger groupId, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        Page<Group> groupList = qGroupRepository.findAllExceptForMySpot(groupId, limit, page, pageable);

        // 기업 정보 dto 맵핑하기
        List<GroupListDto.GroupInfoList> groupListDtoList = new ArrayList<>();
        if (groupList != null && !groupList.isEmpty()) {
            List<BigInteger> managerIds = groupList.stream()
                    .filter(group -> group instanceof Corporation)
                    .map(group -> ((Corporation) group).getManagerId())
                    .filter(Objects::nonNull)
                    .toList();
            List<User> users = (managerIds.isEmpty()) ? null : qUserRepository.getUserAllById(managerIds);
            for (Group group : groupList) {
                User managerUser = null;
                if (group instanceof Corporation corporation && corporation.getManagerId() != null) {
                    managerUser = (users != null) ? users.stream().filter(user -> user.getId().equals(corporation.getManagerId())).findFirst().orElse(null) : null;
                }
                GroupListDto.GroupInfoList corporationListDto = groupMapper.toGroupListDto(group, managerUser);
                groupListDtoList.add(corporationListDto);
            }
        }

        List<Group> groups = groupRepository.findAll();
        GroupListDto groupListDto = GroupListDto.createGroupListDto(groups, groupListDtoList);

        return ItemPageableResponseDto.<GroupListDto>builder().items(groupListDto)
                .limit(pageable.getPageSize()).total(Objects.requireNonNull(groupList).getTotalPages())
                .count(groupList.getNumberOfElements()).build();
    }

    @Override
    @Transactional
    public void saveCorporationList(List<GroupListDto.GroupInfoList> groupListDtoList) throws ParseException {
        Set<BigInteger> groupIdList = new HashSet<>();
        for (GroupListDto.GroupInfoList groupExcelRequestDto : groupListDtoList) {
            groupIdList.add(groupExcelRequestDto.getId());
        }
        List<Group> groupList = qGroupRepository.findAllByIds(groupIdList);

        List<Group> newGroupList = new ArrayList<>();
        List<MealInfo> newMealInfoList = new ArrayList<>();
        // 그룹이 있는지 찾아보기
        for (GroupListDto.GroupInfoList groupInfoList : groupListDtoList) {
            Group group = groupList.stream().filter(groupMatch -> groupMatch.getId().equals(groupInfoList.getId())).findFirst().orElse(null);
            Address address = new Address(groupInfoList.getZipCode(), groupInfoList.getAddress1(), groupInfoList.getAddress2(), groupInfoList.getLocation());

            // 겹치는 요일이 있으면 패스
            List<Days> serviceDays = DaysUtil.serviceDaysToDaysList(groupInfoList.getServiceDays());

            // group 없으면
            if (group == null) {
                Group newGroup = groupMapper.toEntity(groupInfoList);
                newGroupList.add(newGroup);

                List<DiningType> diningTypeList = newGroup.getDiningTypes();
                List<MealInfo> mealInfos = new ArrayList<>();
                for (DiningType diningType : diningTypeList) {
                    Optional<GroupListDto.MealInfo> mealInfo = groupInfoList.getMealInfos().stream()
                            .filter(v -> diningType.getCode().equals(v.getDiningType()))
                            .findAny();
                    mealInfo.ifPresent(v -> mealInfos.add(groupMapper.toMealInfo(mealInfo.get(), newGroup)));
                }
                newMealInfoList.addAll(mealInfos);

            }
            // group 있으면
            else {
                List<DiningType> diningTypeList = DiningTypesUtils.codesToDiningTypes(groupInfoList.getDiningTypes());

                // group update
                if (group instanceof Corporation corporation) {
                    groupMapper.updateCorporation(groupInfoList, corporation);
                } else if (group instanceof OpenGroup openGroup) {
                    openGroup.updateOpenSpot(address, diningTypeList, groupInfoList.getName(), groupInfoList.getEmployeeCount(), true);
                }

                // dining type 체크해서 있으면 업데이트, 없으면 생성
                List<MealInfo> mealInfoList = group.getMealInfos();
                for (DiningType diningType : diningTypeList) {
                    MealInfo mealInfo = mealInfoList.stream().filter(m -> m.getDiningType().equals(diningType)).findAny().orElse(null);
                    if (mealInfo == null) {
                        GroupListDto.MealInfo mealInfoDto = groupInfoList.getMealInfos().stream().filter(v -> v.getDiningType().equals(diningType.getCode()))
                                .findAny().orElse(null);
                        MealInfo newMealInfo = groupMapper.toMealInfo(mealInfoDto, group);
                        newMealInfoList.add(newMealInfo);
                    } else {
                        if (mealInfo instanceof CorporationMealInfo corporationMealInfo) {
                            GroupListDto.MealInfo mealInfoDto = groupInfoList.getMealInfos().stream().filter(v -> v.getDiningType().equals(diningType.getCode()))
                                    .findAny().orElse(null);
                            List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = groupMapper.toServiceDaysAndSupportPrice(mealInfoDto.getSupportPriceByDays());
                            corporationMealInfo.updateServiceDaysAndSupportPrice(serviceDays, serviceDaysAndSupportPriceList);
                        } else mealInfo.updateMealInfo(serviceDays);
                    }
                }

            }
        }

        groupRepository.saveAll(newGroupList);
        mealInfoRepository.saveAll(newMealInfoList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupListDto.GroupInfoList> getAllGroupForExcel() {
        List<Group> groupAllList = groupRepository.findAll();
        // 기업 정보 dto 맵핑하기
        List<GroupListDto.GroupInfoList> groupListDtoList = new ArrayList<>();

        if (groupAllList.isEmpty()) {
            return groupListDtoList;
        }

        List<BigInteger> managerIds = groupAllList.stream()
                .filter(group -> group instanceof Corporation)
                .map(group -> ((Corporation) group).getManagerId())
                .filter(Objects::nonNull)
                .toList();
        List<User> users = (managerIds.isEmpty()) ? null : qUserRepository.getUserAllById(managerIds);
        for (Group group : groupAllList) {
            User managerUser = null;
            if (group instanceof Corporation corporation && corporation.getManagerId() != null) {
                managerUser = (users != null) ? users.stream().filter(user -> user.getId().equals(corporation.getManagerId())).findFirst().orElse(null) : null;
            }
            GroupListDto.GroupInfoList corporationListDto = groupMapper.toGroupListDto(group, managerUser);
            groupListDtoList.add(corporationListDto);
        }

        return groupListDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public GroupListDto.GroupInfoList getGroupDetail(BigInteger groupId) {
        //spotId로 spot 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

        User manager = null;
        if (group instanceof Corporation corporation) {
            if (corporation.getManagerId() != null) {
                manager = userRepository.findById(corporation.getManagerId()).orElse(null);
            }
        }
        return groupMapper.toGroupListDto(group, manager);
    }

    @Override
    @Transactional
    // TODO: 스팟으로 설정되어 있지만 그룹으로 변경
    public void updateGroupDetail(GroupListDto.GroupInfoList groupInfoList) throws ParseException {
        // 그룹 찾기.
        Group group = groupRepository.findById(groupInfoList.getId()).orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));
        List<DiningType> diningTypeList = DiningTypesUtils.codesToDiningTypes(groupInfoList.getDiningTypes());

        Address address = new Address(groupInfoList.getZipCode(), groupInfoList.getAddress1(), groupInfoList.getAddress2(), groupInfoList.getLocation());

        // 겹치는 요일이 있으면 패스
        List<Days> serviceDays = DaysUtil.serviceDaysToDaysList(groupInfoList.getServiceDays());

        // group update
        if (group instanceof Corporation corporation) {
            groupMapper.updateCorporation(groupInfoList, corporation);
        } else if (group instanceof OpenGroup openGroup) {
            openGroup.updateOpenSpot(address, diningTypeList, groupInfoList.getName(), groupInfoList.getEmployeeCount(), true);
        }

        // dining type 체크해서 있으면 업데이트, 없으면 생성
        List<MealInfo> mealInfoList = group.getMealInfos();
        List<MealInfo> newMealInfoList = new ArrayList<>();
        for (DiningType diningType : diningTypeList) {
            MealInfo mealInfo = mealInfoList.stream().filter(m -> m.getDiningType().equals(diningType)).findAny().orElse(null);
            if (mealInfo == null) {
                GroupListDto.MealInfo mealInfoDto = groupInfoList.getMealInfos().stream().filter(v -> v.getDiningType().equals(diningType.getCode()))
                        .findAny().orElse(null);
                MealInfo newMealInfo = groupMapper.toMealInfo(mealInfoDto, group);
                newMealInfoList.add(newMealInfo);
            } else {
                if (mealInfo instanceof CorporationMealInfo corporationMealInfo) {
                    GroupListDto.MealInfo mealInfoDto = groupInfoList.getMealInfos().stream().filter(v -> v.getDiningType().equals(diningType.getCode()))
                            .findAny().orElse(null);
                    List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = groupMapper.toServiceDaysAndSupportPrice(mealInfoDto.getSupportPriceByDays());
                    corporationMealInfo.updateServiceDaysAndSupportPrice(serviceDays, serviceDaysAndSupportPriceList);
                } else mealInfo.updateMealInfo(serviceDays);
            }
        }
        mealInfoRepository.saveAll(newMealInfoList);

    }

    @Override
    @Transactional(readOnly = true)
    public FilterDto getAllListForFilter(Map<String, Object> parameters) {
        String city = parameters.get("city") == null || !parameters.containsKey("city") ? null : qRegionRepository.findCityNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("city"))));
        String county = parameters.get("county") == null || !parameters.containsKey("county") ? null : qRegionRepository.findCountyNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("county"))));
        List<String> villages = parameters.get("villages") == null || !parameters.containsKey("villages") ? null : qRegionRepository.findVillageNameById(StringUtils.parseBigIntegerList((String) parameters.get("villages")));
        // 시/도, 군/구, 동/읍/리 별로 필터. - 군/구, 동/읍/리는 다중 필터 가능
        List<FilterInfo> nameList = qMySpotZoneRepository.findAllNameList();
        Map<BigInteger, String> cityList = qRegionRepository.findAllCity();
        Map<BigInteger, String> countyList = qRegionRepository.findAllCountyByCity(city);
        Map<BigInteger, String> villageList = qRegionRepository.findAllVillageByCounty(city, county);
        Map<BigInteger, String> zipcodeList = qRegionRepository.findAllZipcodeByCityAndCountyAndVillage(city, county, villages);
        List<MySpotZoneStatus> statusDtoList = List.of(MySpotZoneStatus.class.getEnumConstants());

        return mySpotZoneMapper.toFilterDto(nameList, cityList, countyList, villageList, zipcodeList, statusDtoList);
    }

    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<AdminListResponseDto> getAllMySpotZoneList(Map<String, Object> parameters, Integer limit, Integer size, OffsetBasedPageRequest pageable) {
        List<String> name = parameters.get("name") == null || !parameters.containsKey("name") ? null : qMySpotZoneRepository.findNameById(StringUtils.parseBigIntegerList((String) parameters.get("name")));
        String city = parameters.get("city") == null || !parameters.containsKey("city") ? null : qRegionRepository.findCityNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("city"))));
        String county = parameters.get("county") == null || !parameters.containsKey("county") ? null : qRegionRepository.findCountyNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("county"))));
        List<String> villages = parameters.get("villages") == null || !parameters.containsKey("villages") ? null : qRegionRepository.findVillageNameById(StringUtils.parseBigIntegerList((String) parameters.get("villages")));
        List<String> zipcodes = parameters.get("zipcode") == null || !parameters.containsKey("zipcode") ? null : qRegionRepository.findZipcodeById(StringUtils.parseBigIntegerList((String) parameters.get("zipcode")));
        MySpotZoneStatus status = parameters.get("status") == null || !parameters.containsKey("status") ? null : MySpotZoneStatus.ofCode(Integer.parseInt((String) parameters.get("status")));

        Page<MySpotZone> mySpotZoneList = qMySpotZoneRepository.findAllMySpotZone(name, city, county, villages, zipcodes, status, limit, size, pageable);

        List<AdminListResponseDto> adminListResponseDtoList = new ArrayList<>();
        if (mySpotZoneList == null || mySpotZoneList.isEmpty()) {
            return ListItemResponseDto.<AdminListResponseDto>builder().items(adminListResponseDtoList).count(0).limit(pageable.getPageSize()).offset(pageable.getOffset()).total(0L).build();
        }

        List<Region> regions = qRegionRepository.findRegionByMySpotZone(mySpotZoneList.stream().map(MySpotZone::getId).toList());

        adminListResponseDtoList.addAll(mySpotZoneList.stream().map(mySpotZone -> mySpotZoneMapper.toAdminListResponseDto(mySpotZone, regions)).toList());

        return ListItemResponseDto.<AdminListResponseDto>builder().items(adminListResponseDtoList).count(mySpotZoneList.getNumberOfElements())
                .limit(pageable.getPageSize()).offset(pageable.getOffset()).total((long) mySpotZoneList.getTotalPages()).build();
    }

    @Override
    @Transactional
    public void createMySpotZone(CreateRequestDto createRequestDto) {
        // zipcode를 가지고 있는지 확인.
        MySpotZone existMySpotZone = qMySpotZoneRepository.findExistMySpotZoneByZipcodes(createRequestDto.getZipcodes());
        if (existMySpotZone != null) throw new ApiException(ExceptionEnum.ALREADY_EXIST_MY_SPOT_ZONE);

        // 해당 zipcode region 찾기
        List<Region> regions = qRegionRepository.findRegionByZipcodesAndCountiesAndVillages(createRequestDto.getZipcodes(), createRequestDto.getCounties(), createRequestDto.getVillages());
        if (regions == null || regions.isEmpty()) throw new ApiException(ExceptionEnum.NOT_FOUND_REGION);

        // my spot zone 생성
        MySpotZone mySpotZone = mySpotZoneMapper.toMySpotZone(createRequestDto);
        groupRepository.save(mySpotZone);

        // region updqte my sopt zone fk
        regions.forEach(region -> region.updateMySpotZone(mySpotZone.getId()));

        // meal info 생성
        String defaultTime = "00:00";
        String defaultDays = "월, 화, 수, 목, 금, 토, 일";

        List<MealInfo> mealInfoList = mySpotZone.getDiningTypes().stream()
                .map(diningType -> {
                    List<LocalTime> mealTime = switch (diningType) {
                        case MORNING -> createRequestDto.getBreakfastDeliveryTime().stream().map(DateUtils::stringToLocalTime).toList();
                        case LUNCH -> createRequestDto.getLunchDeliveryTime().stream().map(DateUtils::stringToLocalTime).toList();
                        case DINNER -> createRequestDto.getDinnerDeliveryTime().stream().map(DateUtils::stringToLocalTime).toList();
                    };
                    return mySpotZoneMealInfoMapper.toMealInfo(mySpotZone, diningType, mealTime, defaultTime, defaultDays, defaultTime);
                })
                .collect(Collectors.toList());

        mealInfoRepository.saveAll(mealInfoList);
    }

    @Override
    @Transactional
    public void updateMySpotZone(UpdateRequestDto updateRequestDto) {
        // my spot zone 찾기
        MySpotZone mySpotZone = qMySpotZoneRepository.findMySpotZoneById(updateRequestDto.getId());
        if (mySpotZone == null) throw new ApiException(ExceptionEnum.NOT_FOUND_MY_SPOT_ZONE);

        // my spot zone 수정
        mySpotZoneMapper.updateMySpotZone(updateRequestDto, mySpotZone);

        // region list 수정
        List<Region> defaultRegion = qRegionRepository.findRegionByMySpotZoneId(mySpotZone.getId());
        defaultRegion.forEach(region -> region.updateMySpotZone(null));
        List<Region> updateRequestRegion = qRegionRepository.findRegionByZipcodesAndCountiesAndVillages(updateRequestDto.getZipcodes(), updateRequestDto.getCounties(), updateRequestDto.getVillages());
        updateRequestRegion.forEach(region -> region.updateMySpotZone(mySpotZone.getId()));

        // meal info 수정
        mySpotZone.getDiningTypes()
                .forEach(diningType -> {
                    List<LocalTime> deliveryTimes = switch (diningType) {
                        case MORNING -> updateRequestDto.getBreakfastDeliveryTime().stream().map(time -> DateUtils.stringToTime(time, ":")).toList();
                        case LUNCH -> updateRequestDto.getLunchDeliveryTime().stream().map(time -> DateUtils.stringToTime(time, ":")).toList();
                        case DINNER -> updateRequestDto.getDinnerDeliveryTime().stream().map(time -> DateUtils.stringToTime(time, ":")).toList();
                    };
                    mySpotZone.getMealInfo(diningType).updateDeliveryTimes(deliveryTimes);
                });
    }

    @Override
    @Transactional
    public void deleteMySpotZone(List<BigInteger> id) {
        // my spot zone 찾기
        List<MySpotZone> mySpotZoneList = qMySpotZoneRepository.findAllMySpotZoneByIds(id);
        if (mySpotZoneList == null || mySpotZoneList.isEmpty())
            throw new ApiException(ExceptionEnum.NOT_FOUND_MY_SPOT_ZONE);

        // region의 my spot zone fk도 null
        List<Region> regions = qRegionRepository.findRegionByMySpotZone(mySpotZoneList.stream().map(MySpotZone::getId).toList());
        regions.forEach(region -> region.updateMySpotZone(null));

        // my spot zone fk를 가진 my spot 찾아서 null
        List<MySpot> mySpotList = mySpotZoneList.stream().flatMap(v -> v.getSpots().stream().map(s -> (MySpot) s)).toList();
        if (mySpotList.isEmpty()) mySpotZoneList.forEach(mySpotZone -> mySpotZone.updateIsActive(false));
        else {
            mySpotList.forEach(MySpot::updateMySpotForDelete);
            // my spot zone update isActive false
            mySpotZoneList.forEach(mySpotZone -> mySpotZone.updateIsActive(false));
        }
    }

    @Override
    @Transactional
    public void updateLocation() throws ParseException {
        List<Group> groupList = qGroupRepository.findGroupAndAddressIsNull();

        for(Group group : groupList) {
            Map<String, String> updateLocation = AddressUtil.getLocation(group.getAddress().getAddress1());
            group.getAddress().updateLocation(updateLocation.get("location"));
            group.getAddress().updateAddress3(updateLocation.get("jibunAddress"));
        }
    }

    @Override
    @Transactional
    public void updateMySpotZoneStatus(UpdateStatusDto updateStatusDto) {
        List<MySpotZone> mySpotZoneList = qMySpotZoneRepository.findAllMySpotZoneByIds(updateStatusDto.getIds());
        mySpotZoneList.forEach(mySpotZone -> mySpotZoneMapper.updateMySpotZoneStatusAndDate(updateStatusDto, mySpotZone));
    }


}
