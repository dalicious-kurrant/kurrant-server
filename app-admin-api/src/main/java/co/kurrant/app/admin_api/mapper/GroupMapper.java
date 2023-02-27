package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.client.GroupListDto;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {GroupDto.class, DateUtils.class})
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
    GroupDto.DiningType diningTypeToDto (DiningType diningType);

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
        groupDto.setGroup(groupToDto(group));
        groupDto.setSpots(spotsToDtos(group.getSpots()));
        groupDto.setDiningTypes(diningTypesToDtos(group.getDiningTypes()));
        groupDto.setUsers(usersToDtos(users));
        return groupDto;
    }
    @Mapping(source = "corporation.id", target = "id")
    @Mapping(source = "corporation.code", target = "code")
    @Mapping(source = "corporation.name", target = "name")
    @Mapping(source = "corporation.address.zipCode", target = "zipCode")
    @Mapping(source = "corporation.address.address1", target = "address1")
    @Mapping(source = "corporation.address.address2", target = "address2")
    @Mapping( target = "location", expression = "java(String.valueOf(corporation.getAddress().getLocation()))")
    @Mapping(source = "corporation.diningTypes", target = "diningTypes", qualifiedByName = "getDiningCodeList")
    @Mapping(source = "corporation.spots", target = "serviceDays", qualifiedByName = "serviceDayToString")
    @Mapping(source = "managerUser.name", target = "managerName")
    @Mapping(source = "managerUser.phone", target = "managerPhone")
    @Mapping(source = "corporation.isMembershipSupport", target = "isMembershipSupport")
    @Mapping(source = "corporation.employeeCount", target = "employeeCount")
    @Mapping(source = "corporation.isSetting", target = "isSetting")
    @Mapping(source = "corporation.isGarbage", target = "isGarbage")
    @Mapping(source = "corporation.isHotStorage", target = "isHotStorage")
    @Mapping(target = "createdDateTime", expression = "java(DateUtils.toISO(corporation.getCreatedDateTime()))")
    @Mapping(target = "updatedDateTime", expression = "java(DateUtils.toISO(corporation.getUpdatedDateTime()))")
    GroupListDto toCorporationListDto(Corporation corporation, User managerUser);

    @Mapping(source = "apartment.id", target = "id")
    @Mapping(source = "apartment.name", target = "name")
    @Mapping(source = "apartment.address.zipCode", target = "zipCode")
    @Mapping(source = "apartment.address.address1", target = "address1")
    @Mapping(source = "apartment.address.address2", target = "address2")
    @Mapping( target = "location", expression = "java(String.valueOf(apartment.getAddress().getLocation()))")
    @Mapping(source = "apartment.diningTypes", target = "diningTypes", qualifiedByName = "getDiningCodeList")
    @Mapping(source = "apartment.spots", target = "serviceDays", qualifiedByName = "serviceDayToString")
    @Mapping(source = "managerUser.name", target = "managerName")
    @Mapping(source = "managerUser.phone", target = "managerPhone")
    @Mapping(source = "apartment.familyCount", target = "employeeCount")
    @Mapping(target = "createdDateTime", expression = "java(DateUtils.toISO(apartment.getCreatedDateTime()))")
    @Mapping(target = "updatedDateTime", expression = "java(DateUtils.toISO(apartment.getUpdatedDateTime()))")
    GroupListDto toApartmentListDto(Apartment apartment, User managerUser);

    @Named("getDiningCodeList")
    default List<Integer> getDiningCodeList(List<DiningType> diningTypeList) {
        return diningTypeList.stream().map(DiningType::getCode).toList();
    }

    @Named("serviceDayToString")
    default String serviceDayToString(List<Spot> spotList) {
        StringBuilder mealInfoBuilder = new StringBuilder();
        HashSet<String> serviceDayList = new HashSet<>();
        for(Spot spot : spotList) {
            List<MealInfo> mealInfoList = spot.getMealInfos();
            for(MealInfo mealInfo : mealInfoList) {
                List<String> useDays = List.of(mealInfo.getServiceDays().split(", "));
                serviceDayList.addAll(useDays);
            }
        }
        serviceDayList.forEach(mealInfoBuilder::append);
        return String.valueOf(mealInfoBuilder);
    }

}

