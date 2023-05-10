package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {GroupDto.class, DateUtils.class, BigDecimal.class})
public interface GroupMapper {
    @Mapping(source = "id", target = "groupId")
    @Mapping(source = "name", target = "groupName")
    @Named("toIdAndNameDto")
    GroupDto.Group groupToDto(Group group);

    @Mapping(source = "id", target = "spotId")
    @Mapping(source = "name", target = "spotName")
    GroupDto.Spot spotToDto(Spot spot);

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
                .map(this::spotToDto)
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
        if(group instanceof Corporation corporation) {
            groupType = GroupDataType.CORPORATION.getCode();
            employeeCount = corporation.getEmployeeCount();
            groupInfoList.setIsPrepaid(corporation.getIsPrepaid());
            groupInfoList.setMinimumSpend(corporation.getMinimumSpend());
            groupInfoList.setMaximumSpend(corporation.getMaximumSpend());
        }
        else if(group instanceof Apartment apartment) {
            groupType = GroupDataType.APARTMENT.getCode();
            employeeCount = apartment.getFamilyCount();
        }
        else if(group instanceof OpenGroup openGroup) {
            groupType = GroupDataType.OPEN_GROUP.getCode();
            employeeCount = openGroup.getOpenGroupUserCount();
        }

        groupInfoList.setGroupType(groupType);
        groupInfoList.setEmployeeCount(employeeCount);

        groupInfoList.setCode((isCorporation) ? ((Corporation) group).getCode() : null);
        groupInfoList.setName(group.getName());
        groupInfoList.setZipCode(group.getAddress().getZipCode());
        groupInfoList.setAddress1(group.getAddress().getAddress1());
        groupInfoList.setAddress2(group.getAddress().getAddress2());
        groupInfoList.setLocation((group.getAddress().getLocation() != null) ? String.valueOf(group.getAddress().getLocation()) : null);

        List<DiningType> diningTypeList = group.getDiningTypes();
        groupInfoList.setDiningTypes(diningTypeList.stream().map(DiningType::getCode).toList());
        if(managerUser != null) {
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
        List<Days> notSupportDays = new ArrayList<>();
        BigDecimal morningSupportPrice = BigDecimal.ZERO;
        BigDecimal lunchSupportPrice = BigDecimal.ZERO;
        BigDecimal dinnerSupportPrice = BigDecimal.ZERO;

        for(MealInfo mealInfo : mealInfoList) {
            serviceDays = mealInfo.getServiceDays();
            if(mealInfo instanceof CorporationMealInfo corporationMealInfo && diningTypeList.contains(mealInfo.getDiningType())) {
                List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = corporationMealInfo.getServiceDaysAndSupportPrices();
                for(ServiceDaysAndSupportPrice serviceDaysAndSupportPrice : serviceDaysAndSupportPriceList) {
                    List<Days> days = serviceDaysAndSupportPrice.getSupportDays();
                    BigDecimal supportPrice = serviceDaysAndSupportPrice.getSupportPrice();

                    switch (mealInfo.getDiningType()) {
                        case MORNING -> morningSupportPrice = morningSupportPrice.add(supportPrice);
                        case LUNCH -> lunchSupportPrice = lunchSupportPrice.add(supportPrice);
                        case DINNER -> dinnerSupportPrice = dinnerSupportPrice.add(supportPrice);
                    }

                    notSupportDays = serviceDays.stream().filter(d -> !days.contains(d)).toList();
                }
            }
        }
        List<Days> supportDays = new ArrayList<>(serviceDays);
        supportDays.removeAll(notSupportDays);
        groupInfoList.setServiceDays(DaysUtil.serviceDaysToDaysString(serviceDays));
        groupInfoList.setSupportDays(DaysUtil.serviceDaysToDaysString(supportDays));
        groupInfoList.setNotSupportDays(DaysUtil.serviceDaysToDaysString(notSupportDays));
        groupInfoList.setMorningSupportPrice(morningSupportPrice);
        groupInfoList.setLunchSupportPrice(lunchSupportPrice);
        groupInfoList.setDinnerSupportPrice(dinnerSupportPrice);

        return groupInfoList;
    }

    default Group saveToEntity(GroupExcelRequestDto groupInfoList, Address address) {
        List<DiningType> diningTypeList = getDiningType(groupInfoList.getDiningTypes());

        if(GroupDataType.CORPORATION.equals(GroupDataType.ofCode(groupInfoList.getGroupType()))) {
            return Corporation.builder()
                    .address(address)
                    .diningTypes(diningTypeList)
                    .name(groupInfoList.getName())
                    .managerId(groupInfoList.getManagerId())
                    .code(createCode(groupInfoList.getCode()))
                    .isMembershipSupport(!groupInfoList.getIsMembershipSupport().equals("미지원"))
                    .employeeCount(groupInfoList.getEmployeeCount())
                    .isGarbage(!groupInfoList.getIsGarbage().equals("미사용"))
                    .isHotStorage(!groupInfoList.getIsHotStorage().equals("미사용"))
                    .isSetting(!groupInfoList.getIsSetting().equals("미사용"))
                    .minimumSpend(setMinimumSpend(groupInfoList))
                    .maximumSpend(setMaximumSpend(groupInfoList))
                    .build();
        }
        else if(GroupDataType.APARTMENT.equals(GroupDataType.ofCode(groupInfoList.getGroupType()))) {
            return Apartment.builder()
                    .address(address)
                    .diningTypes(diningTypeList)
                    .name(groupInfoList.getName())
                    .managerId(groupInfoList.getManagerId())
                    .familyCount(groupInfoList.getEmployeeCount())
                    .build();
        }
        else if(GroupDataType.OPEN_GROUP.equals(GroupDataType.ofCode(groupInfoList.getGroupType()))) {
            return OpenGroup.builder()
                    .address(address)
                    .diningTypes(diningTypeList)
                    .name(groupInfoList.getName())
                    .managerId(groupInfoList.getManagerId())
                    .openGroupUserCount(groupInfoList.getEmployeeCount())
                    .build();
        }
        return null;
    }


    @Named("createCode")
    default String createCode(String code){
        //65~90
        int leftLimit = 65; // 영 대문자 65~90
        int rightLimit = 90;
        int targetStringLength = 6; // 길이제한
        Random random = new Random();

        String generatedString = random.ints(leftLimit,rightLimit + 1)
                .filter(i -> (i >= 65) && (i <= 90))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }


    @Named("setMinimumSpend")
    default BigDecimal setMinimumSpend(GroupExcelRequestDto groupInfoList) {
        if(groupInfoList.getMinimumSpend() != null) {
            BigDecimal minimumSpend = BigDecimal.ZERO;
            return minimumSpend.add(BigDecimal.valueOf(groupInfoList.getMinimumSpend()));
        }
        return null;
    }

    @Named("setMaximumSpend")
    default BigDecimal setMaximumSpend(GroupExcelRequestDto groupInfoList) {
        if(groupInfoList.getMaximumSpend() != null) {
            BigDecimal maximumSpend = BigDecimal.ZERO;
            return maximumSpend.add(BigDecimal.valueOf(groupInfoList.getMaximumSpend()));
        }
        return null;
    }

    @Named("getDiningType")
    default List<DiningType> getDiningType(List<String> diningTypeInteger) {
        List<DiningType> diningTypeList = new ArrayList<>();
        if (diningTypeInteger != null && !diningTypeInteger.isEmpty()) {
            for (String diningTypeCode : diningTypeInteger) {
                diningTypeList.add(DiningType.ofString(diningTypeCode));
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
        // 기업 스팟인 경우
        if (group instanceof Corporation corporation) {
            return CorporationMealInfo.builder()
                    .group(corporation)
                    .diningType(diningType)
                    .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                    .deliveryTime(DateUtils.stringToLocalTime(deliveryTime))
                    .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                    .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                    .serviceDaysAndSupportPrices(serviceDaysAndSupportPriceList)
                    .build();
        } else if (group instanceof Apartment apartment) {
            return ApartmentMealInfo.builder()
                    .group(apartment)
                    .diningType(diningType)
                    .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                    .deliveryTime(DateUtils.stringToLocalTime(deliveryTime))
                    .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                    .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                    .build();
        } else if (group instanceof OpenGroup openGroup) {
            return OpenGroupMealInfo.builder()
                    .group(openGroup)
                    .diningType(diningType)
                    .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                    .deliveryTime(DateUtils.stringToLocalTime(deliveryTime))
                    .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                    .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                    .build();

        }
        return null;
    }
}

