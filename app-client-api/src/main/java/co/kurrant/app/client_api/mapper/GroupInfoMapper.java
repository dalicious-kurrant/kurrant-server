package co.kurrant.app.client_api.mapper;

import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.geo.Point;

import java.util.HashSet;
import java.util.List;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface GroupInfoMapper {
    @Mapping(source = "group.id", target = "id")
    @Mapping(source = "group", target = "code", qualifiedByName = "getGroupCode")
    @Mapping(source = "group.name", target = "name")
    @Mapping(source = "group.address.zipCode", target = "zipCode")
    @Mapping(source = "group.address.address1", target = "address1")
    @Mapping(source = "group.address.address2", target = "address2")
    @Mapping(target = "location", expression = "java(String.valueOf(group.getAddress().getLocation()))")
    @Mapping(source = "group.diningTypes", target = "diningTypes", qualifiedByName = "getDiningCodeList")
    @Mapping(source = "group.spots", target = "serviceDays", qualifiedByName = "serviceDayToString")
    @Mapping(source = "managerUser.name", target = "managerName")
    @Mapping(source = "managerUser.phone", target = "managerPhone")
    @Mapping(source = "group", target = "isMembershipSupport", qualifiedByName = "getIsMembershipSupport")
    @Mapping(source = "group", target = "employeeCount", qualifiedByName = "getEmployeeCount")
    @Mapping(source = "group", target = "isSetting", qualifiedByName = "getIsSetting")
    @Mapping(source = "group", target = "isGarbage", qualifiedByName = "getIsGarbage")
    @Mapping(source = "group", target = "isHotStorage", qualifiedByName = "getIsHotStorage")
    GroupListDto.GroupInfoList toCorporationListDto(Group group, User managerUser);

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
                if(mealInfo.getServiceDays() == null || mealInfo.getServiceDays().isEmpty() || mealInfo.getServiceDays().isBlank()) continue;
                List<String> useDays = List.of(mealInfo.getServiceDays().split(", "));
                serviceDayList.addAll(useDays);
            }
        }
        serviceDayList.forEach(day -> mealInfoBuilder.append(day).append(", "));
        return String.valueOf(mealInfoBuilder);
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

}
