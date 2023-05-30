package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.domain.client.dto.filter.*;
import co.dalicious.domain.client.dto.mySpotZone.AdminListResponseDto;
import co.dalicious.domain.client.dto.mySpotZone.CreateRequestDto;
import co.dalicious.domain.client.dto.mySpotZone.UpdateRequestDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.domain.client.mapper.MealInfoMapper;
import co.dalicious.domain.client.mapper.MySpotZoneMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.order.repository.QMembershipSupportPriceRepository;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.DiningTypesUtils;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.dalicious.domain.client.dto.UpdateSpotDetailResponseDto;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.SpotMapper;
import co.kurrant.app.admin_api.service.GroupService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
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
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;
    private final SpotMapper spotMapper;
    private final QMembershipSupportPriceRepository qmembershipSupportPriceRepository;
    private final MySpotZoneMapper mySpotZoneMapper;
    private final QMySpotZoneRepository qMySpotZoneRepository;
    private final QRegionRepository qRegionRepository;
    private final MealInfoMapper mealInfoMapper;

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
        Page<Group> groupList = qGroupRepository.findAll(groupId, limit, page, pageable);

        // 기업 정보 dto 맵핑하기
        List<GroupListDto.GroupInfoList> groupListDtoList = new ArrayList<>();
        if(groupList != null && !groupList.isEmpty()) {
            List<BigInteger> managerIds = groupList.stream()
                    .filter(group -> group instanceof Corporation)
                    .map(group -> ((Corporation) group).getManagerId())
                    .filter(Objects::nonNull)
                    .toList();
            List<User> users = (managerIds.isEmpty()) ? null : qUserRepository.getUserAllById(managerIds);
            for(Group group : groupList) {
                User managerUser = null;
                if(group instanceof Corporation corporation && corporation.getManagerId() != null) {
                    managerUser = (users != null) ? users.stream().filter(user -> user.getId().equals(corporation.getManagerId())).findFirst().orElse(null) : null;
                }
                GroupListDto.GroupInfoList corporationListDto = groupMapper.toCorporationListDto(group, managerUser);
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
    public void saveCorporationList(List<GroupExcelRequestDto> groupListDtoList) throws ParseException {
        Set<BigInteger> groupIdList = new HashSet<>();
        for(GroupExcelRequestDto groupExcelRequestDto : groupListDtoList) {
            groupIdList.add(groupExcelRequestDto.getId());
        }
        List<Group> groupList = qGroupRepository.findAllByIds(groupIdList);

        List<Group> newGroupList = new ArrayList<>();
        List<MealInfo> newMealInfoList = new ArrayList<>();
        // 그룹이 있는지 찾아보기
        for(GroupExcelRequestDto groupInfoList : groupListDtoList) {
            Group group = groupList.stream().filter(groupMatch -> groupMatch.getId().equals(groupInfoList.getId())).findFirst().orElse(null);
            Address address = new Address(groupInfoList.getZipCode(), groupInfoList.getAddress1(), groupInfoList.getAddress2(), groupInfoList.getLocation());

            // 겹치는 요일이 있으면 패스
            List<Days> notSupportDays = groupInfoList.getNotSupportDays() != null ? DaysUtil.serviceDaysToDaysList(groupInfoList.getNotSupportDays()) : new ArrayList<>();
            List<Days> serviceDays = DaysUtil.serviceDaysToDaysList(groupInfoList.getServiceDays());
            List<Days> supportDays = new ArrayList<>(serviceDays);
            supportDays.removeAll(notSupportDays);

            // group 없으면
            if(group == null) {
                Group newGroup = groupMapper.saveToEntity(groupInfoList, address);
                newGroupList.add(newGroup);

                List<DiningType> diningTypeList = newGroup.getDiningTypes();
                for(DiningType diningType : diningTypeList) {
                    List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = new ArrayList<>();

                    Integer supportPrice = null;
                    if(diningType.equals(DiningType.MORNING)) supportPrice = groupInfoList.getMorningSupportPrice();
                    else if(diningType.equals(DiningType.LUNCH)) supportPrice = groupInfoList.getLunchSupportPrice();
                    else if(diningType.equals(DiningType.DINNER)) supportPrice = groupInfoList.getDinnerSupportPrice();
                    serviceDaysAndSupportPriceList.add(groupMapper.toServiceDaysAndSupportPriceEntity(supportDays, BigDecimal.valueOf(supportPrice)));

                    MealInfo mealInfo = groupMapper.toMealInfo(newGroup, diningType, "00:00", "00:00", groupInfoList.getServiceDays(), "00:00", serviceDaysAndSupportPriceList);
                    newMealInfoList.add(mealInfo);
                }
            }
            // group 있으면
            else {
                List<DiningType> diningTypeList = new ArrayList<>();
                List<String> integerList = groupInfoList.getDiningTypes();
                for(String string : integerList) {
                    diningTypeList.add(DiningType.ofString(string));
                }

                // group update
                if(group instanceof Corporation corporation) {
                    corporation.updateCorporation(groupInfoList, address, diningTypeList);
                }
                else if (group instanceof Apartment apartment) {
                    apartment.updateApartment(address, diningTypeList, groupInfoList.getName(), groupInfoList.getEmployeeCount(), true);
                }
                else if (group instanceof  OpenGroup openGroup) {
                    openGroup.updateOpenSpot(address, diningTypeList, groupInfoList.getName(), groupInfoList.getEmployeeCount(), true);
                }

                // dining type 체크해서 있으면 업데이트, 없으면 생성
                List<MealInfo> mealInfoList = group.getMealInfos();
                for(DiningType diningType : diningTypeList) {
                    Integer supportPrice = null;
                    if(diningType.equals(DiningType.MORNING)) supportPrice = groupInfoList.getMorningSupportPrice();
                    else if(diningType.equals(DiningType.LUNCH)) supportPrice = groupInfoList.getLunchSupportPrice();
                    else if(diningType.equals(DiningType.DINNER)) supportPrice = groupInfoList.getDinnerSupportPrice();

                    List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = new ArrayList<>();
                    if(supportPrice != 0) serviceDaysAndSupportPriceList.add(groupMapper.toServiceDaysAndSupportPriceEntity(supportDays, BigDecimal.valueOf(supportPrice)));

                    MealInfo mealInfo = mealInfoList.stream().filter(m -> m.getDiningType().equals(diningType)).findAny().orElse(null);
                    if(mealInfo == null) {
                        MealInfo newMealInfo = groupMapper.toMealInfo(group, diningType, "00:00", "00:00", groupInfoList.getServiceDays(), "00:00", serviceDaysAndSupportPriceList);
                        newMealInfoList.add(newMealInfo);
                    } else {
                        if(mealInfo instanceof  CorporationMealInfo corporationMealInfo) corporationMealInfo.updateServiceDaysAndSupportPrice(serviceDays, serviceDaysAndSupportPriceList);
                        else mealInfo.updateMealInfo(serviceDays);
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

        if(groupAllList.isEmpty()) { return groupListDtoList; }

        List<BigInteger> managerIds = groupAllList.stream()
                .filter(group -> group instanceof Corporation)
                .map(group -> ((Corporation) group).getManagerId())
                .filter(Objects::nonNull)
                .toList();
        List<User> users = (managerIds.isEmpty()) ? null : qUserRepository.getUserAllById(managerIds);
        for(Group group : groupAllList) {
            User managerUser = null;
            if(group instanceof Corporation corporation && corporation.getManagerId() != null) {
                managerUser = (users != null) ? users.stream().filter(user -> user.getId().equals(corporation.getManagerId())).findFirst().orElse(null) : null;
            }
            GroupListDto.GroupInfoList corporationListDto = groupMapper.toCorporationListDto(group, managerUser);
            groupListDtoList.add(corporationListDto);
        }

        return groupListDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public UpdateSpotDetailResponseDto getGroupDetail(Integer spotId) {
        //spotId로 spot 조회
        Group group = groupRepository.findById(BigInteger.valueOf(spotId))
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

        if (group instanceof Corporation corporation){
            List<MealInfo> mealInfoList = group.getMealInfos();

            if (corporation.getManagerId() != null) {
                User manager = userRepository.findById(corporation.getManagerId()).orElse(null);
                return spotMapper.toDetailDto(group, manager, mealInfoList);
            }
            return spotMapper.toDetailDto(group, User.builder().id(BigInteger.valueOf(0)).phone("없음").name("없음").build(), mealInfoList);
        }

        return spotMapper.toDetailDto(group, User.builder().id(BigInteger.valueOf(0)).phone("없음").name("없음").build(), null);
    }

    @Override
    @Transactional
    // TODO: 스팟으로 설정되어 있지만 그룹으로 변경
    public void updateGroupDetail(UpdateSpotDetailRequestDto updateSpotDetailRequestDto) throws ParseException {
        // 그룹 찾기.
        Group group = groupRepository.findById(updateSpotDetailRequestDto.getSpotId()).orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));

        // 스팟에 해당하는 다이닝 타입 변경
        List<DiningType> updateDiningTypeList = DiningTypesUtils.stringCodeToDiningTypes(updateSpotDetailRequestDto.getDiningTypes());
        group.updateDiningTypes(updateDiningTypeList);

        List<Days> notSupportDays = updateSpotDetailRequestDto.getNotSupportDays() != null ? DaysUtil.serviceDaysToDaysList(updateSpotDetailRequestDto.getNotSupportDays()) : new ArrayList<>();
        List<Days> serviceDays = DaysUtil.serviceDaysToDaysList(updateSpotDetailRequestDto.getServiceDays());
        List<Days> supportDays = new ArrayList<>(serviceDays);
        supportDays.removeAll(notSupportDays);

        // dining type 체크해서 있으면 업데이트, 없으면 생성
        List<MealInfo> mealInfoList = group.getMealInfos();
        List<MealInfo> newMealInfoList = new ArrayList<>();
        for(DiningType diningType : updateDiningTypeList) {
            BigDecimal supportPrice = null;
            if(diningType.equals(DiningType.MORNING)) supportPrice = updateSpotDetailRequestDto.getBreakfastSupportPrice();
            else if(diningType.equals(DiningType.LUNCH)) supportPrice = updateSpotDetailRequestDto.getLunchSupportPrice();
            else if(diningType.equals(DiningType.DINNER)) supportPrice = updateSpotDetailRequestDto.getDinnerSupportPrice();

            List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = new ArrayList<>();
            if(supportPrice != null && supportPrice.compareTo(BigDecimal.valueOf(0)) != 0) serviceDaysAndSupportPriceList.add(groupMapper.toServiceDaysAndSupportPriceEntity(supportDays, supportPrice));

            MealInfo mealInfo = mealInfoList.stream().filter(m -> m.getDiningType().equals(diningType)).findAny().orElse(null);
            if(mealInfo == null) {
                MealInfo newMealInfo = groupMapper.toMealInfo(group, diningType, "00:00", "00:00", updateSpotDetailRequestDto.getServiceDays(), "00:00", serviceDaysAndSupportPriceList);
                newMealInfoList.add(newMealInfo);
            } else {
                if(mealInfo instanceof  CorporationMealInfo corporationMealInfo) corporationMealInfo.updateServiceDaysAndSupportPrice(serviceDays,serviceDaysAndSupportPriceList);
                else mealInfo.updateMealInfo(serviceDays);
            }
        }

        Address address = new Address(updateSpotDetailRequestDto.getZipCode(), updateSpotDetailRequestDto.getAddress1(), updateSpotDetailRequestDto.getAddress2(), updateSpotDetailRequestDto.getLocation().equals("없음") ? null : updateSpotDetailRequestDto.getLocation());

        if(group instanceof Corporation corporation) {
            LocalDate membershipEndDate = corporation.getMembershipEndDate();
            LocalDate updateMembershipEndDate = DateUtils.stringToDate(updateSpotDetailRequestDto.getMembershipEndDate());
            if(corporation.getIsMembershipSupport() && updateSpotDetailRequestDto.getMembershipEndDate() != null && !updateSpotDetailRequestDto.getMembershipEndDate().isEmpty()) {
                // 멤버십 종료날짜가 새로 생성 또는 기존 날짜보다 이전으로 업데이트 한 경우
                if(membershipEndDate == null || updateMembershipEndDate.isBefore(membershipEndDate)) {
                    List<Membership> memberships = qmembershipSupportPriceRepository.findAllByGroupAndNow(corporation);
                    for (Membership membership : memberships) {
                        if(membership.getEndDate().isAfter(updateMembershipEndDate)) {
                            membership.updateEndDate(updateMembershipEndDate);
                        }
                    }
                }
                // 멤버십 종료날짜가 기존 날짜 이후로 업데이트 된 경우
                if(membershipEndDate != null && updateMembershipEndDate.isAfter(membershipEndDate)) {
                    List<Membership> memberships = qmembershipSupportPriceRepository.findAllByGroupAndNow(corporation);
                    for (Membership membership : memberships) {
                        LocalDate limitEndDate = membership.getStartDate().plusMonths(1);
                        if(limitEndDate.isBefore(updateMembershipEndDate)) {
                            membership.updateEndDate(updateMembershipEndDate);
                        }
                    }
                }
            }
            corporation.updateCorporation(updateSpotDetailRequestDto, address, updateDiningTypeList);
            corporation.updatePrepaidCategories(spotMapper.toPrepaidCategories(updateSpotDetailRequestDto.getPrepaidCategoryList()));
        }
        else if (group instanceof Apartment apartment) {
            apartment.updateApartment(address, updateDiningTypeList, updateSpotDetailRequestDto.getSpotName(), updateSpotDetailRequestDto.getEmployeeCount(), updateSpotDetailRequestDto.getIsActive());
        }
        else if (group instanceof  OpenGroup openGroup) {
            openGroup.updateOpenSpot(address, updateDiningTypeList, updateSpotDetailRequestDto.getSpotName(), updateSpotDetailRequestDto.getEmployeeCount(), updateSpotDetailRequestDto.getIsActive());
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
        List<FilterInfo> cityList = qRegionRepository.findAllCity();
        List<FilterInfo> countyList = qRegionRepository.findAllCountyByCity(city);
        List<FilterInfo> villageList = qRegionRepository.findAllVillageByCounty(city, county);
        List<FilterInfo> zipcodeList = qRegionRepository.findAllZipcodeByCityAndCountyAndVillage(city, county, villages);
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
        if(mySpotZoneList == null || mySpotZoneList.isEmpty()) {
            return ListItemResponseDto.<AdminListResponseDto>builder().items(adminListResponseDtoList).count(0).limit(pageable.getPageSize()).offset(pageable.getOffset()).total(0L).build();
        }

        adminListResponseDtoList.addAll(mySpotZoneList.stream().map(mySpotZoneMapper::toAdminListResponseDto).toList());

        return ListItemResponseDto.<AdminListResponseDto>builder().items(adminListResponseDtoList).count(mySpotZoneList.getNumberOfElements())
                .limit(pageable.getPageSize()).offset(pageable.getOffset()).total((long) mySpotZoneList.getTotalPages()).build();
    }

    @Override
    @Transactional
    public void createMySpotZone(CreateRequestDto createRequestDto) {
        // zipcode를 가지고 있는지 확인.
        MySpotZone existMySpotZone = qMySpotZoneRepository.findExistMySpotZoneByZipcodes(createRequestDto.getZipcodes());
        if(existMySpotZone != null) throw new ApiException(ExceptionEnum.ALREADY_EXIST_MY_SPOT_ZONE);

        // 해당 zipcode region 찾기
        List<Region> regions = qRegionRepository.findRegionByZipcodesAndCountiesAndVillages(createRequestDto.getZipcodes(), createRequestDto.getCounties(), createRequestDto.getVillages());
        if(regions == null || regions.isEmpty()) throw new ApiException(ExceptionEnum.NOT_FOUND_REGION);

        // my spot zone 생성
        MySpotZone mySpotZone = mySpotZoneMapper.toMySpotZone(createRequestDto);

        // region updqte my sopt zone fk
        regions.forEach(region -> region.updateMySpotZone(mySpotZone));

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

                    return mealInfoMapper.toMealInfo(mySpotZone, diningType, mealTime, defaultTime, defaultDays, defaultTime);
                })
                .collect(Collectors.toList());

        groupRepository.save(mySpotZone);
        mealInfoRepository.saveAll(mealInfoList);
    }

    @Override
    @Transactional
    public void updateMySpotZone(UpdateRequestDto updateRequestDto) {
        // my spot zone 찾기
        MySpotZone mySpotZone = qMySpotZoneRepository.findMySpotZoneById(updateRequestDto.getId());
        if(mySpotZone == null) throw new ApiException(ExceptionEnum.NOT_FOUND_MY_SPOT_ZONE);

        // my spot zone 수정
        mySpotZone.updateMySpotZone(updateRequestDto);

        // region list 수정
        List<Region> defaultRegion = mySpotZone.getRegionList();
        defaultRegion.forEach(region -> region.updateMySpotZone(null));
        List<Region> updateRequestRegion = qRegionRepository.findRegionByZipcodesAndCountiesAndVillages(updateRequestDto.getZipcodes(), updateRequestDto.getCounties(), updateRequestDto.getVillages());
        updateRequestRegion.forEach(region -> region.updateMySpotZone(mySpotZone));

        // meal info 수정
        mySpotZone.getDiningTypes()
                .forEach(diningType -> {
                    List<LocalTime> deliveryTimes = switch (diningType) {
                        case MORNING -> updateRequestDto.getBreakfastDeliveryTime().stream().map(DateUtils::stringToLocalTime).toList();
                        case LUNCH -> updateRequestDto.getLunchDeliveryTime().stream().map(DateUtils::stringToLocalTime).toList();
                        case DINNER -> updateRequestDto.getDinnerDeliveryTime().stream().map(DateUtils::stringToLocalTime).toList();
                    };
                    mySpotZone.getMealInfo(diningType).updateDeliveryTimes(deliveryTimes);
                });

    }


}
