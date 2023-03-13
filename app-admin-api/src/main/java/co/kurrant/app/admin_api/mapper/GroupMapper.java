package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.kurrant.app.admin_api.model.enums.GroupDataType;
import exception.ApiException;
import exception.ExceptionEnum;
import jdk.jfr.Name;
import org.hibernate.query.criteria.internal.path.SetAttributeJoin;
import org.mapstruct.*;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalTime;
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

    @Mapping(source = "group.id", target = "id")
    @Mapping(source = "group", target = "groupType", qualifiedByName = "getGroupDataType")
    @Mapping(source = "group", target = "code", qualifiedByName = "getGroupCode")
    @Mapping(source = "group.name", target = "name")
    @Mapping(source = "group.address.zipCode", target = "zipCode")
    @Mapping(source = "group.address.address1", target = "address1")
    @Mapping(source = "group.address.address2", target = "address2")
    @Mapping(source = "group", target = "location", qualifiedByName = "getLocation")
    @Mapping(source = "group.diningTypes", target = "diningTypes", qualifiedByName = "getDiningCodeList")
    @Mapping(source = "group.spots", target = "serviceDays", qualifiedByName = "serviceDayToString")
    @Mapping(source = "managerUser.id", target = "managerId")
    @Mapping(source = "managerUser.name", target = "managerName")
    @Mapping(source = "managerUser.phone", target = "managerPhone")
    @Mapping(source = "group", target = "isMembershipSupport", qualifiedByName = "getIsMembershipSupport")
    @Mapping(source = "group", target = "employeeCount", qualifiedByName = "getEmployeeCount")
    @Mapping(source = "group", target = "isSetting", qualifiedByName = "getIsSetting")
    @Mapping(source = "group", target = "isGarbage", qualifiedByName = "getIsGarbage")
    @Mapping(source = "group", target = "isHotStorage", qualifiedByName = "getIsHotStorage")
    @Mapping(target = "morningSupportPrice", expression = "java(getSupportPrice(group, DiningType.MORNING))")
    @Mapping(target = "lunchSupportPrice", expression = "java(getSupportPrice(group, DiningType.LUNCH))")
    @Mapping(target = "dinnerSupportPrice", expression = "java(getSupportPrice(group, DiningType.DINNER))")
    @Mapping(source = "group", target = "minimumSpend", qualifiedByName = "getMinimumSpend")
    @Mapping(source = "group", target = "maximumSpend", qualifiedByName = "getMaximumSpend")
    GroupListDto.GroupInfoList toCorporationListDto(Group group, User managerUser);

    @Named("getLocation")
    default String getLocation(Group group) {
        if (group.getAddress().getLocation() != null) {
            return String.valueOf(group.getAddress().getLocation());
        }
        return null;
    }

    @Named("getGroupDataType")
    default Integer getGroupDataType(Group group) {
        Integer groupType = null;
        if (group instanceof Corporation) groupType = GroupDataType.CORPORATION.getCode();
        else if (group instanceof Apartment) groupType = GroupDataType.APARTMENT.getCode();
        else if (group instanceof OpenGroup) groupType = GroupDataType.OPEN_SPOT.getCode();
        return groupType;
    }


    @Named("getMinimumSpend")
    default BigDecimal getMinimumSpend(Group group) {
        BigDecimal minimumSpend = BigDecimal.ZERO;
        if (group instanceof Corporation corporation) {
            return minimumSpend = corporation.getMinimumSpend();
        }
        return minimumSpend;
    }

    @Named("getMaximumSpend")
    default BigDecimal getMaximumSpend(Group group) {
        BigDecimal maximumSpend = BigDecimal.ZERO;
        if (group instanceof Corporation corporation) {
            return maximumSpend = corporation.getMaximumSpend();
        }
        return maximumSpend;
    }

    @Named("getSupportPrice")
    default BigDecimal getSupportPrice(Group group, DiningType type) {
        if (group instanceof Corporation) {
            for (MealInfo mealInfo : group.getMealInfos()) {
                if (mealInfo.getDiningType().equals(type)) return ((CorporationMealInfo) mealInfo).getSupportPrice();
            }
        }
        return null;
    }

    @Named("getDiningCodeList")
    default List<Integer> getDiningCodeList(List<DiningType> diningTypeList) {
        return diningTypeList.stream().map(DiningType::getCode).toList();
    }

    @Named("serviceDayToString")
    default String serviceDayToString(List<Spot> spotList) {
        StringBuilder mealInfoBuilder = new StringBuilder();
        List<String> serviceDayList = new ArrayList<>();
        for (Spot spot : spotList) {
            List<DiningType> diningTypes = spot.getDiningTypes();
            List<MealInfo> mealInfoList = spot.getMealInfos();
            for (MealInfo mealInfo : mealInfoList) {
                if (mealInfo.getServiceDays() == null || mealInfo.getServiceDays().isEmpty() || mealInfo.getServiceDays().isBlank()) {
                    continue;
                }
                List<String> useDays = List.of(mealInfo.getServiceDays().split(", |,"));
                serviceDayList.addAll(useDays);
            }
        }
        serviceDayList.stream().distinct().forEach(day -> mealInfoBuilder.append(day).append(", "));
        if (mealInfoBuilder.length() != 0) {
            return String.valueOf(mealInfoBuilder).substring(0, mealInfoBuilder.length() - 2);
        }
        return null;

    }

    @Named("getGroupCode")
    default String getGroupCode(Group group) {
        if (group instanceof Corporation corporation) return corporation.getCode();
        else return null;
    }

    @Named("getEmployeeCount")
    default Integer getEmployeeCount(Group group) {
        if (group instanceof Corporation corporation) return corporation.getEmployeeCount();
        else if (group instanceof Apartment apartment) return apartment.getFamilyCount();
        else if (group instanceof OpenGroup openGroup) return openGroup.getOpenGroupUserCount();
        else return null;
    }

    @Named("getIsSetting")
    default Boolean getIsSetting(Group group) {
        if (group instanceof Corporation corporation) return corporation.getIsSetting();
        else return null;
    }

    @Named("getIsGarbage")
    default Boolean getIsGarbage(Group group) {
        if (group instanceof Corporation corporation) return corporation.getIsGarbage();
        else return null;
    }

    @Named("getIsHotStorage")
    default Boolean getIsHotStorage(Group group) {
        if (group instanceof Corporation corporation) return corporation.getIsHotStorage();
        else return null;
    }

    @Named("getIsMembershipSupport")
    default Boolean getIsMembershipSupport(Group group) {
        if (group instanceof Corporation corporation) return corporation.getIsMembershipSupport();
        else return null;
    }

    @Mapping(source = "address", target = "address")
    @Mapping(source = "groupInfoList.diningTypes", target = "diningTypes", qualifiedByName = "getDiningType")
    @Mapping(source = "groupInfoList.name", target = "name")
    @Mapping(source = "groupInfoList.managerId", target = "managerId")
    @Mapping(source = "groupInfoList.code", target = "code", qualifiedByName = "createCode")
    @Mapping(source = "groupInfoList", target = "isMembershipSupport", qualifiedByName = "isMembershipSupport")
    @Mapping(source = "groupInfoList.employeeCount", target = "employeeCount")
    @Mapping(source = "groupInfoList", target = "isGarbage", qualifiedByName = "isGarbage")
    @Mapping(source = "groupInfoList", target = "isHotStorage", qualifiedByName = "isHotStorage")
    @Mapping(source = "groupInfoList", target = "isSetting", qualifiedByName = "isSetting")
    @Mapping(source = "groupInfoList", target = "minimumSpend", qualifiedByName = "setMinimumSpend")
    @Mapping(source = "groupInfoList", target = "maximumSpend", qualifiedByName = "setMaximumSpend")
    Corporation groupInfoListToCorporationEntity(GroupExcelRequestDto groupInfoList, Address address);


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

    @Named("isMembershipSupport")
    default Boolean isMembershipSupport(GroupExcelRequestDto groupInfoList) {
        Boolean result = null;
        if (Objects.equals(groupInfoList.getIsMembershipSupport(), "미지원")) result = false;
        else if (Objects.equals(groupInfoList.getIsMembershipSupport(), "지원")) result = true;
        return result;
    }

    @Named("isGarbage")
    default Boolean isGarbage(GroupExcelRequestDto groupInfoList) {
        Boolean result = null;
        if (Objects.equals(groupInfoList.getIsGarbage(), "미사용")) result = false;
        else if (Objects.equals(groupInfoList.getIsGarbage(), "사용")) result = true;
        return result;
    }

    @Named("isHotStorage")
    default Boolean isHotStorage(GroupExcelRequestDto groupInfoList) {
        Boolean result = null;
        if (Objects.equals(groupInfoList.getIsHotStorage(), "미사용")) result = false;
        else if (Objects.equals(groupInfoList.getIsHotStorage(), "사용")) result = true;
        return result;
    }

    @Named("isSetting")
    default Boolean isSetting(GroupExcelRequestDto groupInfoList) {
        Boolean result = null;
        if (Objects.equals(groupInfoList.getIsSetting(), "미사용")) result = false;
        else if (Objects.equals(groupInfoList.getIsSetting(), "사용")) result = true;
        return result;
    }

    @Mapping(source = "address", target = "address")
    @Mapping(source = "groupInfoList.diningTypes", target = "diningTypes", qualifiedByName = "getDiningType")
    @Mapping(source = "groupInfoList.name", target = "name")
    @Mapping(source = "groupInfoList.managerId", target = "managerId")
    @Mapping(source = "groupInfoList.employeeCount", target = "familyCount")
    Apartment groupInfoListToApartmentEntity(GroupExcelRequestDto groupInfoList, Address address);

    @Mapping(source = "address", target = "address")
    @Mapping(source = "groupInfoList.diningTypes", target = "diningTypes", qualifiedByName = "getDiningType")
    @Mapping(source = "groupInfoList.name", target = "name")
    @Mapping(source = "groupInfoList.managerId", target = "managerId")
    @Mapping(source = "groupInfoList.employeeCount", target = "openGroupUserCount")
    OpenGroup groupInfoListToOpenGroupEntity(GroupExcelRequestDto groupInfoList, Address address);

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
}

