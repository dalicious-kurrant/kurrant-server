package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.address.utils.AddressUtil;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.user.entity.User;
import co.dalicious.integration.client.user.entity.MySpot;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.DiningTypesUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import org.locationtech.jts.io.ParseException;
import org.mapstruct.*;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {GroupDto.class, DateUtils.class, BigDecimal.class, DiningTypesUtils.class, AddressUtil.class})
public interface GroupMapper {
    @Mapping(source = "id", target = "groupId")
    @Mapping(source = "name", target = "groupName")
    @Named("toIdAndNameDto")
    GroupDto.Group groupToDto(Group group);

    @Mapping(source = "id", target = "spotId")
    @Mapping(source = "name", target = "spotName")
    GroupDto.Spot spotToDto(Spot spot);

    default GroupDto.Spot mySpotToDto(MySpot mySpot) {
        GroupDto.Spot spotDto = new GroupDto.Spot();
        spotDto.setSpotId(mySpot.getId());
        spotDto.setSpotName(mySpot.getAddress().addressToString());
        return spotDto;
    }

    @Mapping(source = "diningType", target = "diningType")
    @Mapping(source = "code", target = "code")
    GroupDto.DiningType diningTypeToDto(DiningType diningType);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "name", target = "userName")
    GroupDto.User userToDto(User user);

    default List<GroupDto.Group> groupsToDtos(List<? extends Group> groups) {
        return groups.stream()
                .map(this::groupToDto)
                .collect(Collectors.toList());
    }

    default List<GroupDto.Spot> spotsToDtos(List<Spot> spots) {
        return spots.stream()
                .map(spot -> spot instanceof MySpot mySpot ? mySpotToDto(mySpot) : spotToDto(spot))
                .collect(Collectors.toList());
    }

    default List<GroupDto.DiningType> diningTypesToDtos(List<DiningType> diningTypes) {
        return diningTypes.stream()
                .map(this::diningTypeToDto)
                .collect(Collectors.toList());
    }

    default List<GroupDto.User> usersToDtos(List<User> users) {
        return users.stream()
                .map(this::userToDto)
                .collect(Collectors.toList());
    }

    default GroupDto groupToGroupDto(Group group, List<User> users) {
        GroupDto groupDto = new GroupDto();
        if (group != null) {
            groupDto.setGroup(groupToDto(group));
            groupDto.setSpots(spotsToDtos(group.getSpots()));
            groupDto.setDiningTypes(diningTypesToDtos(group.getDiningTypes()));
        }
        groupDto.setUsers(usersToDtos(users));
        return groupDto;
    }

    default GroupListDto.GroupInfoList toCorporationListDto(Group group, User managerUser) {
        GroupListDto.GroupInfoList groupInfoList = new GroupListDto.GroupInfoList();
        boolean isCorporation = group instanceof Corporation;

        groupInfoList.setId(group.getId());

        Integer groupType = null;
        Integer employeeCount = null;
        if (group instanceof Corporation corporation) {
            groupType = GroupDataType.CORPORATION.getCode();
            employeeCount = corporation.getEmployeeCount();
            groupInfoList.setMembershipEndDate(DateUtils.format(corporation.getMembershipEndDate()));
            groupInfoList.setIsPrepaid(corporation.getIsPrepaid());
            groupInfoList.setMinimumSpend(corporation.getMinimumSpend());
            groupInfoList.setMaximumSpend(corporation.getMaximumSpend());
        } else if (group instanceof OpenGroup openGroup) {
            groupType = GroupDataType.OPEN_GROUP.getCode();
            employeeCount = openGroup.getOpenGroupUserCount();
        }

        groupInfoList.setGroupType(groupType);
        groupInfoList.setEmployeeCount(employeeCount);
        groupInfoList.setIsActive(group.getIsActive());
        groupInfoList.setCode((isCorporation) ? ((Corporation) group).getCode() : null);
        groupInfoList.setName(group.getName());
        groupInfoList.setZipCode(group.getAddress() == null ? null : group.getAddress().getZipCode());
        groupInfoList.setAddress1(group.getAddress() == null ? null : group.getAddress().getAddress1());
        groupInfoList.setAddress2(group.getAddress() == null ? null : group.getAddress().getAddress2());
        groupInfoList.setLocation(group.getAddress() == null ? null : group.getAddress().getLocation() != null ? String.valueOf(group.getAddress().getLocation()) : null);

        List<DiningType> diningTypeList = group.getDiningTypes();
        groupInfoList.setDiningTypes(diningTypeList.stream().map(DiningType::getCode).toList());
        if (managerUser != null) {
            groupInfoList.setManagerId(managerUser.getId());
            groupInfoList.setManagerName(managerUser.getName());
            groupInfoList.setManagerPhone(managerUser.getPhone());
        }
        groupInfoList.setIsMembershipSupport((isCorporation) ? ((Corporation) group).getIsMembershipSupport() : null);
        groupInfoList.setIsGarbage((isCorporation) ? ((Corporation) group).getIsGarbage() : null);
        groupInfoList.setIsHotStorage((isCorporation) ? ((Corporation) group).getIsHotStorage() : null);
        groupInfoList.setIsSetting((isCorporation) ? ((Corporation) group).getIsSetting() : null);

        List<MealInfo> mealInfoList = group.getMealInfos();
        List<Days> serviceDays = new ArrayList<>();

        List<GroupListDto.MealInfo> mealInfoDtos = new ArrayList<>();
        for (MealInfo mealInfo : mealInfoList) {
            serviceDays = mealInfo.getServiceDays();
            GroupListDto.MealInfo mealInfoDto = toMealInfoDto(mealInfo);
            if (mealInfo instanceof CorporationMealInfo corporationMealInfo) {
                List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = corporationMealInfo.getServiceDaysAndSupportPrices();
                List<GroupListDto.SupportPriceByDay> supportPriceByDays = toSupportPriceByDays(serviceDaysAndSupportPriceList);
                mealInfoDto.setSupportPriceByDays(supportPriceByDays);
            }
            mealInfoDtos.add(mealInfoDto);
        }
        groupInfoList.setServiceDays(DaysUtil.serviceDaysToDaysString(serviceDays));
        groupInfoList.setMealInfos(mealInfoDtos);

        return groupInfoList;
    }

    @Mapping(source = "address.zipCode", target = "zipCode")
    @Mapping(source = "address.address1", target = "address1")
    @Mapping(source = "address.address2", target = "address2")
    @Mapping(target = "location", expression = "java(corporation.getAddress().getLocation() == null ? null : corporation.getAddress().getLocation().toString())")
    @Mapping(target = "diningTypes", expression = "java(DiningTypesUtils.diningTypesToCodes(corporation.getDiningTypes()))")
    @Mapping(source = "deliveryFeeOption.deliveryFeeOption", target = "deliveryFeeOption")
    @Mapping(target = "membershipEndDate", expression = "java(DateUtils.localDateToString(corporation.getMembershipEndDate()))")
    GroupListDto.GroupInfoList toCorporationDto(Corporation corporation);

    @Mapping(target = "membershipEndDate", expression = "java(DateUtils.stringToDate(groupDto.getMembershipEndDate()))")
    @Mapping(target = "diningTypes", expression = "java(DiningTypesUtils.codesToDiningTypes(groupDto.getDiningTypes()))")
    @Mapping(target = "deliveryFeeOption", expression = "java(DeliveryFeeOption.ofString(groupDto.getDeliveryFeeOption()))")
    @Mapping(source = "zipCode", target = "address.zipCode")
    @Mapping(source = "address1", target = "address.address1")
    @Mapping(source = "address2", target = "address.address2")
    @Mapping(source = "location", target = "address.location")
    Corporation toCorporation(GroupListDto.GroupInfoList groupDto) throws ParseException;

    @Mapping(source = "address.zipCode", target = "zipCode")
    @Mapping(source = "address.address1", target = "address1")
    @Mapping(source = "address.address2", target = "address2")
    @Mapping(target = "location", expression = "java(openGroup.getAddress().getLocation() == null ? null : openGroup.getAddress().getLocation().toString())")
    @Mapping(source = "openGroupUserCount", target = "employeeCount")
    @Mapping(target = "diningTypes", expression = "java(DiningTypesUtils.diningTypesToCodes(openGroup.getDiningTypes()))")
    GroupListDto.GroupInfoList toOpenSpotDto(OpenGroup openGroup);

    @Mapping(target = "diningTypes", expression = "java(DiningTypesUtils.codesToDiningTypes(groupDto.getDiningTypes()))")
    @Mapping(source = "zipCode", target = "address.zipCode")
    @Mapping(source = "address1", target = "address.address1")
    @Mapping(source = "address2", target = "address.address2")
    @Mapping(source = "location", target = "address.location")
    @Mapping(source = "employeeCount", target = "openGroupUserCount")
    OpenGroup toOpenGroup(GroupListDto.GroupInfoList groupDto) throws ParseException;

    default GroupListDto.GroupInfoList toGroupListDto(Group group, User managerUser) {
        GroupListDto.GroupInfoList groupInfoList = null;
        if(group instanceof Corporation corporation) {
            groupInfoList = toCorporationDto(corporation);
            groupInfoList.setGroupType(GroupDataType.CORPORATION.getCode());
        }
        if(group instanceof OpenGroup openGroup) {
            groupInfoList = toOpenSpotDto(openGroup);
            groupInfoList.setGroupType(GroupDataType.OPEN_GROUP.getCode());
        }
        if(managerUser != null) {
            groupInfoList.setManagerId(managerUser.getId());
            groupInfoList.setManagerName(managerUser.getName());
            groupInfoList.setManagerPhone(managerUser.getPhone());
        }

        Set<Days> serviceDays = new HashSet<>();

        List<GroupListDto.MealInfo> mealInfoDtos = new ArrayList<>();
        for (MealInfo mealInfo : group.getMealInfos()) {
            serviceDays.addAll(mealInfo.getServiceDays());
            GroupListDto.MealInfo mealInfoDto = toMealInfoDto(mealInfo);
            if (mealInfo instanceof CorporationMealInfo corporationMealInfo) {
                List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = corporationMealInfo.getServiceDaysAndSupportPrices();
                List<GroupListDto.SupportPriceByDay> supportPriceByDays = toSupportPriceByDays(serviceDaysAndSupportPriceList);
                mealInfoDto.setSupportPriceByDays(supportPriceByDays);
            }
            mealInfoDtos.add(mealInfoDto);
        }
        groupInfoList.setServiceDays(DaysUtil.serviceDaysToDaysString(serviceDays));
        groupInfoList.setMealInfos(mealInfoDtos);

        return groupInfoList;
    }

    default Group toEntity(GroupListDto.GroupInfoList groupDto) throws ParseException {
        if(GroupDataType.ofCode(groupDto.getGroupType()).equals(GroupDataType.CORPORATION)) {
            return toCorporation(groupDto);
        }
        if(GroupDataType.ofCode(groupDto.getGroupType()).equals(GroupDataType.OPEN_GROUP)) {
            return toOpenGroup(groupDto);
        }
        return null;
    }

    default Group saveToEntity(GroupListDto.GroupInfoList groupInfoList, Address address) {
        List<DiningType> diningTypeList = getDiningType(groupInfoList.getDiningTypes());

        if (GroupDataType.CORPORATION.equals(GroupDataType.ofCode(groupInfoList.getGroupType()))) {
            return Corporation.builder()
                    .address(address)
                    .diningTypes(diningTypeList)
                    .name(groupInfoList.getName())
                    .membershipEndDate(DateUtils.stringToDate(groupInfoList.getMembershipEndDate()))
                    .managerId(groupInfoList.getManagerId())
                    .code(createCode(groupInfoList.getCode()))
                    .isMembershipSupport(groupInfoList.getIsMembershipSupport())
                    .employeeCount(groupInfoList.getEmployeeCount())
                    .isGarbage(groupInfoList.getIsGarbage())
                    .isHotStorage(groupInfoList.getIsHotStorage())
                    .isSetting(groupInfoList.getIsSetting())
                    .minimumSpend(groupInfoList.getMinimumSpend())
                    .maximumSpend(groupInfoList.getMaximumSpend())
                    .build();
        } else if (GroupDataType.OPEN_GROUP.equals(GroupDataType.ofCode(groupInfoList.getGroupType()))) {
            return OpenGroup.builder()
                    .address(address)
                    .diningTypes(diningTypeList)
                    .name(groupInfoList.getName())
                    .openGroupUserCount(groupInfoList.getEmployeeCount())
                    .build();
        }
        return null;
    }

    default GroupListDto.MealInfo toMealInfoDto(MealInfo mealInfo) {
        return GroupListDto.MealInfo.builder()
                .diningType(mealInfo.getDiningType().getCode())
                .deliveryTimes(DateUtils.timesToString(mealInfo.getDeliveryTimes()))
                .lastOrderTime(DayAndTime.dayAndTimeToString(mealInfo.getLastOrderTime()))
                .membershipBenefitTime(DayAndTime.dayAndTimeToString(mealInfo.getMembershipBenefitTime()))
                .serviceDays(DaysUtil.serviceDaysToDaysString(mealInfo.getServiceDays()))
                .build();
    }

    default List<GroupListDto.SupportPriceByDay> toSupportPriceByDays(List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPrices) {
        List<GroupListDto.SupportPriceByDay> supportPriceByDays = new ArrayList<>();
        for (ServiceDaysAndSupportPrice serviceDaysAndSupportPrice : serviceDaysAndSupportPrices) {
            supportPriceByDays.addAll(toSupportPriceByDay(serviceDaysAndSupportPrice));
        }
        return supportPriceByDays.stream().sorted(Comparator.comparing(GroupListDto.SupportPriceByDay::getServiceDay))
                .toList();
    }

    default List<GroupListDto.SupportPriceByDay> toSupportPriceByDay(ServiceDaysAndSupportPrice serviceDaysAndSupportPrice) {
        List<GroupListDto.SupportPriceByDay> supportPriceByDays = new ArrayList<>();
        for (Days days : serviceDaysAndSupportPrice.getSupportDays()) {
            supportPriceByDays.add(new GroupListDto.SupportPriceByDay(days.getDays(), serviceDaysAndSupportPrice.getSupportPrice()));
        }
        return supportPriceByDays;
    }

    default List<ServiceDaysAndSupportPrice> toServiceDaysAndSupportPrice(List<GroupListDto.SupportPriceByDay> supportPriceByDays) {
        if(supportPriceByDays == null) return null;
        MultiValueMap<Integer, Days> supportPriceByDayMap = new LinkedMultiValueMap<>();
        for (GroupListDto.SupportPriceByDay supportPriceByDay : supportPriceByDays) {
            supportPriceByDayMap.add(supportPriceByDay.getSupportPrice().intValue(), Days.ofString(supportPriceByDay.getServiceDay()));
        }

        List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = new ArrayList<>();
        for (Integer integer : supportPriceByDayMap.keySet()) {
            if (integer != null && integer != 0) {
                List<Days> days = supportPriceByDayMap.get(integer);
                days = days.stream().sorted(Comparator.comparing(Days::getCode))
                        .toList();
                serviceDaysAndSupportPriceList.add(ServiceDaysAndSupportPrice.builder()
                        .supportDays(days)
                        .supportPrice(BigDecimal.valueOf(integer))
                        .build());
            }
        }
        return serviceDaysAndSupportPriceList;
    }

    @Named("createCode")
    default String createCode(String code) {
        //65~90
        int leftLimit = 65; // 영 대문자 65~90
        int rightLimit = 90;
        int targetStringLength = 6; // 길이제한
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i >= 65) && (i <= 90))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }


    @Named("setMinimumSpend")
    default BigDecimal setMinimumSpend(GroupExcelRequestDto groupInfoList) {
        if (groupInfoList.getMinimumSpend() != null) {
            BigDecimal minimumSpend = BigDecimal.ZERO;
            return minimumSpend.add(BigDecimal.valueOf(groupInfoList.getMinimumSpend()));
        }
        return null;
    }

    @Named("setMaximumSpend")
    default BigDecimal setMaximumSpend(GroupExcelRequestDto groupInfoList) {
        if (groupInfoList.getMaximumSpend() != null) {
            BigDecimal maximumSpend = BigDecimal.ZERO;
            return maximumSpend.add(BigDecimal.valueOf(groupInfoList.getMaximumSpend()));
        }
        return null;
    }

    @Named("getDiningType")
    default List<DiningType> getDiningType(List<Integer> diningTypeInteger) {
        List<DiningType> diningTypeList = new ArrayList<>();
        if (diningTypeInteger != null && !diningTypeInteger.isEmpty()) {
            for (Integer diningTypeCode : diningTypeInteger) {
                diningTypeList.add(DiningType.ofCode(diningTypeCode));
            }
            return diningTypeList;
        }
        return null;
    }

    default ServiceDaysAndSupportPrice toServiceDaysAndSupportPriceEntity(List<Days> supportDays, BigDecimal supportPrice) {
        return ServiceDaysAndSupportPrice.builder()
                .supportPrice(supportPrice)
                .supportDays(supportDays)
                .build();
    }

    default MealInfo toMealInfo(Group group, DiningType diningType, String lastOrderTime, String deliveryTime, String useDays, String membershipBenefitTime, List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList) {
        // MealInfo 를 생성하기 위한 기본값이 존재하지 않으면 객체 생성 X
        if (lastOrderTime == null || deliveryTime == null || useDays == null) {
            return null;
        }

        String[] deliveryTimeStrArr = deliveryTime.split(",|, ");
        List<LocalTime> deliveryTimes = new ArrayList<>();

        for (String deliveryTimeStr : deliveryTimeStrArr) {
            deliveryTimes.add(DateUtils.stringToLocalTime(deliveryTimeStr));
        }

        // 기업 스팟인 경우
        if (group instanceof Corporation corporation) {
            return CorporationMealInfo.builder()
                    .group(corporation)
                    .diningType(diningType)
                    .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                    .deliveryTimes(deliveryTimes)
                    .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                    .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                    .serviceDaysAndSupportPrices(serviceDaysAndSupportPriceList)
                    .build();
        } else if (group instanceof OpenGroup openGroup) {
            return OpenGroupMealInfo.builder()
                    .group(openGroup)
                    .diningType(diningType)
                    .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                    .deliveryTimes(deliveryTimes)
                    .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                    .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                    .build();

        }
        return null;
    }

    default MealInfo toMealInfo(GroupListDto.MealInfo mealInfoDto, Group group) {
        // MealInfo 를 생성하기 위한 기본값이 존재하지 않으면 객체 생성 X
        if (mealInfoDto == null || mealInfoDto.getLastOrderTime() == null || mealInfoDto.getDeliveryTimes() == null) {
            return null;
        }

        List<LocalTime> deliveryTimes = DateUtils.stringToLocalTimes(mealInfoDto.getDeliveryTimes());

        // 기업 스팟인 경우
        if (group instanceof Corporation corporation) {
            List<GroupListDto.SupportPriceByDay> supportPriceByDays = mealInfoDto.getSupportPriceByDays();
            List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = toServiceDaysAndSupportPrice(supportPriceByDays);

            return CorporationMealInfo.builder()
                    .group(corporation)
                    .diningType(DiningType.ofCode(mealInfoDto.getDiningType()))
                    .lastOrderTime(DayAndTime.stringToDayAndTime(mealInfoDto.getLastOrderTime()))
                    .deliveryTimes(deliveryTimes)
                    .serviceDays(DaysUtil.serviceDaysToDaysList(mealInfoDto.getServiceDays()))
                    .membershipBenefitTime(DayAndTime.stringToDayAndTime(mealInfoDto.getMembershipBenefitTime()))
                    .serviceDaysAndSupportPrices(serviceDaysAndSupportPriceList)
                    .build();
        } else if (group instanceof OpenGroup openGroup) {
            return OpenGroupMealInfo.builder()
                    .group(openGroup)
                    .diningType(DiningType.ofCode(mealInfoDto.getDiningType()))
                    .lastOrderTime(DayAndTime.stringToDayAndTime(mealInfoDto.getLastOrderTime()))
                    .deliveryTimes(deliveryTimes)
                    .membershipBenefitTime(DayAndTime.stringToDayAndTime(mealInfoDto.getMembershipBenefitTime()))
                    .serviceDays(DaysUtil.serviceDaysToDaysList(mealInfoDto.getServiceDays()))
                    .build();

        }
        return null;
    }

    default List<MealInfo> toMealInfos(List<GroupListDto.MealInfo> mealInfoDtos, Group group) {
        return mealInfoDtos.stream()
                .filter(v -> group.getDiningTypes().contains(DiningType.ofCode(v.getDiningType())))
                .map(v -> this.toMealInfo(v, group))
                .toList();
    }


    @Mapping(target = "membershipEndDate", expression = "java(DateUtils.stringToDate(groupDto.getMembershipEndDate()))")
    @Mapping(target = "diningTypes", expression = "java(DiningTypesUtils.codesToDiningTypes(groupDto.getDiningTypes()))")
    @Mapping(source = "zipCode", target = "address.zipCode")
    @Mapping(source = "address1", target = "address.address1")
    @Mapping(source = "address2", target = "address.address2")
    @Mapping(target = "deliveryFeeOption", expression = "java(DeliveryFeeOption.ofString(groupDto.getDeliveryFeeOption()))")
    @Mapping(target = "mealInfos", ignore = true)
    void updateCorporation(GroupListDto.GroupInfoList groupDto, @MappingTarget Corporation corporation) throws ParseException;
}

