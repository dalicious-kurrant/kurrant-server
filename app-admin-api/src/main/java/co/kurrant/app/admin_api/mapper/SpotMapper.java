package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.dto.SpotResponseDto;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;

import co.kurrant.app.admin_api.dto.GroupDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.geo.Point;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, Address.class, Group.class})
public interface SpotMapper {
    @Mapping(source = "breakfastSupportPrice", target = "breakfastSupportPrice")
    @Mapping(source = "lunchSupportPrice", target = "lunchSupportPrice")
    @Mapping(source = "dinnerSupportPrice", target = "dinnerSupportPrice")
    @Mapping(source = "breakfastDeliveryTime", target = "breakfastDeliveryTime")
    @Mapping(source = "lunchDeliveryTime", target = "lunchDeliveryTime")
    @Mapping(source = "dinnerDeliveryTime", target = "dinnerDeliveryTime")
    @Mapping(source = "breakfastUseDays", target = "breakfastUseDays")
    @Mapping(source = "lunchUseDays", target = "lunchUseDays")
    @Mapping(source = "dinnerUseDays", target = "dinnerUseDays")
    @Mapping(source = "diningTypeTemp", target="diningType")
    @Mapping(source = "spot.createdDateTime", target = "createdDateTime", qualifiedByName = "createdTimeFormat")
    @Mapping(source = "spot.updatedDateTime", target = "updatedDateTime", qualifiedByName = "updatedTimeFormat")
    @Mapping(source = "spot.address.location", target = "location", qualifiedByName = "getLocation")
    @Mapping(source = "spot.address.zipCode", target = "zipCode")
    @Mapping(source = "spot.address.address2", target = "address2")
    @Mapping(source = "spot.address.address1", target="address1")
    @Mapping(source = "spot.group.name", target = "groupName")
    @Mapping(source = "spot.group.id", target = "groupId")
    @Mapping(source = "spot.name", target = "spotName")
    @Mapping(source = "spot.id", target="spotId")
    SpotResponseDto toDto(Spot spot, String diningTypeTemp,
                          String breakfastUseDays, String breakfastDeliveryTime, BigDecimal breakfastSupportPrice,
                          String lunchUseDays, String lunchDeliveryTime, BigDecimal lunchSupportPrice,
                          String dinnerUseDays, String dinnerDeliveryTime, BigDecimal dinnerSupportPrice, String lastOrderTime);

    @Named("createdTimeFormat")
    default String createdTimeFormat(Timestamp time){
        return DateUtils.format(time, "yyyy-MM-dd");
    }

    @Named("updatedTimeFormat")
    default String updatedTimeFormat(Timestamp time){
        return DateUtils.format(time, "yyyy-MM-dd");
    }

    @Named("getLocation")
    default String getLocation(Point location){
        if (location == null){
            return "null";
        }
        return location.toString();
    }



    @Mapping(source = "address", target = "address")
    @Mapping(source = "spotInfo.groupId", target = "group", qualifiedByName = "generatedGroup")
    @Mapping(source = "spotInfo.spotName", target = "name")
    @Mapping(source = "diningTypes", target = "diningTypes")
    Spot toEntity(SpotResponseDto spotInfo, Address address, List<DiningType> diningTypes);

    @Named("generatedGroup")
    default Group generatedGroup(BigInteger groupId){
        return new Group(groupId);
    }

    @Mapping(source = "", ta)
    Spot toEntity(GroupListDto.GroupInfoList groupInfoList);
}

