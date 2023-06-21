package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.utils.AddressUtil;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.DiningTypesUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import org.locationtech.jts.io.ParseException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
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

    @Mapping(source = "address.zipCode", target = "zipCode")
    @Mapping(source = "address.address1", target = "address1")
    @Mapping(source = "address.address2", target = "address2")
    @Mapping(target = "location", expression = "java(corporation.getAddress().getLocation() == null ? null : corporation.getAddress().locationToString())")
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
    Corporation toCorporation(GroupListDto.GroupInfoList groupDto) throws ParseException;

    @Mapping(source = "address.zipCode", target = "zipCode")
    @Mapping(source = "address.address1", target = "address1")
    @Mapping(source = "address.address2", target = "address2")
    @Mapping(target = "location", expression = "java(openGroup.getAddress().getLocation() == null ? null : openGroup.getAddress().locationToString())")
    @Mapping(source = "openGroupUserCount", target = "employeeCount")
    @Mapping(target = "diningTypes", expression = "java(DiningTypesUtils.diningTypesToCodes(openGroup.getDiningTypes()))")
    GroupListDto.GroupInfoList toOpenSpotDto(OpenGroup openGroup);

    @Mapping(target = "diningTypes", expression = "java(DiningTypesUtils.codesToDiningTypes(groupDto.getDiningTypes()))")
    @Mapping(source = "zipCode", target = "address.zipCode")
    @Mapping(source = "address1", target = "address.address1")
    @Mapping(source = "address2", target = "address.address2")
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
        if(groupInfoList != null) {
            groupInfoList.setServiceDays(DaysUtil.serviceDaysToDaysString(serviceDays));
            groupInfoList.setMealInfos(mealInfoDtos);
        }

        return groupInfoList;
    }

    default Group toEntity(GroupListDto.GroupInfoList groupDto) throws ParseException {
        Group group;
        if(GroupDataType.ofCode(groupDto.getGroupType()).equals(GroupDataType.CORPORATION)) {
            group = toCorporation(groupDto);
            Map<String, String> addressMap = AddressUtil.getLocation(groupDto.getAddress1());
            group.getAddress().setAddress3(addressMap.get("jibunAddress"));
            group.getAddress().setLocation(addressMap.get("location"));
            return group;
        }
        if(GroupDataType.ofCode(groupDto.getGroupType()).equals(GroupDataType.OPEN_GROUP)) {
            group = toOpenGroup(groupDto);
            Map<String, String> addressMap = AddressUtil.getLocation(groupDto.getAddress1());
            group.getAddress().setAddress3(addressMap.get("jibunAddress"));
            group.getAddress().setLocation(addressMap.get("location"));
            return group;
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
        return supportPriceByDays.stream().sorted(Comparator.comparing(v -> Days.ofString(v.getServiceDay())))
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

    @Mapping(target = "membershipEndDate", expression = "java(DateUtils.stringToDate(groupDto.getMembershipEndDate()))")
    @Mapping(target = "diningTypes", expression = "java(DiningTypesUtils.codesToDiningTypes(groupDto.getDiningTypes()))")
    @Mapping(source = "zipCode", target = "address.zipCode")
    @Mapping(source = "address1", target = "address.address1")
    @Mapping(source = "address2", target = "address.address2")
    @Mapping(target = "deliveryFeeOption", expression = "java(DeliveryFeeOption.ofString(groupDto.getDeliveryFeeOption()))")
    @Mapping(target = "mealInfos", ignore = true)
    void updateCorporation(GroupListDto.GroupInfoList groupDto, @MappingTarget Corporation corporation) throws ParseException;

    default void updateMealInfo(GroupListDto.MealInfo mealInfoDto, Group group, @MappingTarget MealInfo mealInfo) {
        if(mealInfo instanceof CorporationMealInfo corporationMealInfo) {
            updateCorporationMealInfo(mealInfoDto, group, corporationMealInfo);
        }
        else {
            DiningType diningType = DiningType.ofCode(mealInfoDto.getDiningType());
            List<LocalTime> deliveryTimes = DateUtils.stringToLocalTimes(mealInfoDto.getDeliveryTimes());
            DayAndTime membershipBenefitTime = DayAndTime.stringToDayAndTime(mealInfoDto.getMembershipBenefitTime());
            DayAndTime lastOrderTime = DayAndTime.stringToDayAndTime(mealInfoDto.getLastOrderTime());
            List<Days> serviceDays = DaysUtil.serviceDaysToDaysList(mealInfoDto.getServiceDays()).stream().sorted().toList();
            mealInfo.updateMealInfo(diningType, deliveryTimes, membershipBenefitTime, lastOrderTime, serviceDays, group);
        }
    }

    default void updateCorporationMealInfo(GroupListDto.MealInfo mealInfoDto, Group group, @MappingTarget CorporationMealInfo mealInfo) {
        List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = toServiceDaysAndSupportPrice(mealInfoDto.getSupportPriceByDays());
        DiningType diningType = DiningType.ofCode(mealInfoDto.getDiningType());
        List<LocalTime> deliveryTimes = DateUtils.stringToLocalTimes(mealInfoDto.getDeliveryTimes());
        DayAndTime membershipBenefitTime = DayAndTime.stringToDayAndTime(mealInfoDto.getMembershipBenefitTime());
        DayAndTime lastOrderTime = DayAndTime.stringToDayAndTime(mealInfoDto.getLastOrderTime());
        List<Days> serviceDays = DaysUtil.serviceDaysToDaysList(mealInfoDto.getServiceDays()).stream().sorted().toList();
        mealInfo.updateCorporationMealInfo(diningType, deliveryTimes, membershipBenefitTime, lastOrderTime, serviceDays, group, serviceDaysAndSupportPriceList);
    };
}

