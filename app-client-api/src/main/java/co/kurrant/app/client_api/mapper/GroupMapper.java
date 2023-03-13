package co.kurrant.app.client_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.ClientType;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.domain.order.dto.GroupDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {GroupDto.class, DateUtils.class, BigDecimal.class})
public interface GroupMapper {

    @Mapping(source = "id", target = "spotId")
    @Mapping(source = "name", target = "spotName")
    GroupDto.Spot spotToDto(Spot spot);

    @Mapping(source = "diningType", target = "diningType")
    @Mapping(source = "code", target = "code")
    GroupDto.DiningType diningTypeToDto (DiningType diningType);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "name", target = "userName")
    GroupDto.User userToDto(User user);

    @Mapping(source = "id", target = "makersId")
    @Mapping(source = "name", target = "makersName")
    GroupDto.Makers makersToDto(Makers makers);


    default GroupDto.User withDrawlUsersToDto(User user) {
        GroupDto.User userDto = new GroupDto.User();
        userDto.setUserId(user.getId());
        userDto.setUserName("(탈퇴) " + user.getName());
        return userDto;
    };

    default List<GroupDto.Spot> spotsToDtos(List<Spot> spots) {
        return spots.stream()
                .map(this::spotToDto)
                .collect(Collectors.toList());
    }

    default List<GroupDto.Makers> makersToDtos(List<Makers> makers) {
        return makers.stream()
                .map(this::makersToDto)
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

    default List<GroupDto.User> usersToDtos(Set<User> users, Set<User> withDrawlUsers) {
        List<GroupDto.User> userDtos = users.stream()
                .map(this::userToDto)
                .collect(Collectors.toList());
        List<GroupDto.User> withDrawlUserDtos = withDrawlUsers.stream()
                .map(this::withDrawlUsersToDto)
                .toList();
        userDtos.addAll(withDrawlUserDtos);
        return userDtos;
    }


    default GroupDto groupToGroupDto(Group group, Set<User> users, Set<User> withDrawlUsers, List<Makers> makers) {
        GroupDto groupDto = new GroupDto();
        if(group != null) {
            groupDto.setMakers(makersToDtos(makers));
            groupDto.setSpots(spotsToDtos(group.getSpots()));
            groupDto.setDiningTypes(diningTypesToDtos(group.getDiningTypes()));
        }
        groupDto.setUsers(usersToDtos(users, withDrawlUsers));
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
        if(group.getAddress().getLocation() != null) {
            return String.valueOf(group.getAddress().getLocation());
        }
        return null;
    }

    @Named("getGroupDataType")
    default Integer getGroupDataType(Group group) {
        Integer groupType = null;
        if(group instanceof Corporation ) groupType = ClientType.CORPORATION.getCode();
        else if(group instanceof Apartment) groupType = ClientType.APARTMENT.getCode();
        else if(group instanceof OpenGroup) groupType = ClientType.OPEN_GROUP.getCode();
        return groupType;
    }


    @Named("getMinimumSpend")
    default BigDecimal getMinimumSpend(Group group) {
        BigDecimal minimumSpend = BigDecimal.ZERO;
        if(group instanceof Corporation corporation) {
            return minimumSpend = corporation.getMinimumSpend();
        }
        return minimumSpend;
    }
    @Named("getMaximumSpend")
    default BigDecimal getMaximumSpend(Group group) {
        BigDecimal maximumSpend = BigDecimal.ZERO;
        if(group instanceof Corporation corporation) {
            return maximumSpend = corporation.getMaximumSpend();
        }
        return maximumSpend;
    }

    @Named("getSupportPrice")
    default BigDecimal getSupportPrice(Group group, DiningType type) {
        if(group instanceof Corporation) {
            List<Spot> spotList = group.getSpots();
            for(Spot spot : spotList) {
                List<MealInfo> mealInfoList = spot.getMealInfos();
                for(MealInfo mealInfo : mealInfoList) {
                    if(mealInfo.getDiningType().equals(type)) return ((CorporationMealInfo) mealInfo).getSupportPrice();
                }
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
        if(mealInfoBuilder.length() != 0) {
            return String.valueOf(mealInfoBuilder).substring(0, mealInfoBuilder.length() - 2);
        }
        return null;

    }

    @Named("getGroupCode")
    default String getGroupCode(Group group) {
        if(group instanceof Corporation corporation) return corporation.getCode();
        else return null;
    }

    @Named("getEmployeeCount")
    default Integer getEmployeeCount(Group group) {
        if(group instanceof Corporation corporation) return corporation.getEmployeeCount();
        else if(group instanceof Apartment apartment) return apartment.getFamilyCount();
        else if(group instanceof OpenGroup openGroup) return openGroup.getOpenGroupUserCount();
        else return null;
    }

    @Named("getIsSetting")
    default Boolean getIsSetting(Group group) {
        if(group instanceof Corporation corporation) return corporation.getIsSetting();
        else return null;
    }

    @Named("getIsGarbage")
    default Boolean getIsGarbage(Group group) {
        if(group instanceof Corporation corporation) return corporation.getIsGarbage();
        else return null;
    }

    @Named("getIsHotStorage")
    default Boolean getIsHotStorage(Group group) {
        if(group instanceof Corporation corporation) return corporation.getIsHotStorage();
        else return null;
    }

    @Named("getIsMembershipSupport")
    default Boolean getIsMembershipSupport(Group group) {
        if(group instanceof Corporation corporation) return corporation.getIsMembershipSupport();
        else return null;
    }

    @Mapping(source = "address", target = "address")
    @Mapping(source = "groupInfoList.diningTypes", target = "diningTypes", qualifiedByName = "getDiningType")
    @Mapping(source = "groupInfoList.name", target = "name")
    @Mapping(source = "groupInfoList.managerId", target = "managerId")
    @Mapping(source = "groupInfoList.code", target = "code")
    @Mapping(source = "groupInfoList", target = "isMembershipSupport", qualifiedByName = "isMembershipSupport")
    @Mapping(source = "groupInfoList.employeeCount", target = "employeeCount")
    @Mapping(source = "groupInfoList", target = "isGarbage", qualifiedByName = "isGarbage")
    @Mapping(source = "groupInfoList", target = "isHotStorage", qualifiedByName = "isHotStorage")
    @Mapping(source = "groupInfoList", target = "isSetting", qualifiedByName = "isSetting")
    @Mapping(source = "groupInfoList", target = "minimumSpend", qualifiedByName = "setMinimumSpend")
    @Mapping(source = "groupInfoList", target = "maximumSpend", qualifiedByName = "setMaximumSpend")
    Corporation groupInfoListToCorporationEntity(GroupExcelRequestDto groupInfoList, Address address);

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
        if(diningTypeInteger != null && !diningTypeInteger.isEmpty()) {
            for(String diningTypeCode : diningTypeInteger) {
                diningTypeList.add(DiningType.ofString(diningTypeCode));
            }
            return diningTypeList;
        }
        return null;
    }
}
