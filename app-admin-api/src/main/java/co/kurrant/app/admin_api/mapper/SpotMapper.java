package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.client.SpotResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.geo.Point;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Mapper(componentModel = "spring", imports = {DateUtils.class, Address.class, Group.class})
public interface SpotMapper {
    @Mapping(source = "morningSupportPrice", target = "morningSupportPrice")
    @Mapping(source = "lunchSupportPrice", target = "lunchSupportPrice")
    @Mapping(source = "dinnerSupportPrice", target = "dinnerSupportPrice")
    @Mapping(source = "morningDeliveryTime", target = "morningDeliveryTime")
    @Mapping(source = "lunchDeliveryTime", target = "lunchDeliveryTime")
    @Mapping(source = "dinnerDeliveryTime", target = "dinnerDeliveryTime")
    @Mapping(source = "morningUseDays", target = "morningUseDays")
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
                          String morningUseDays, String morningDeliveryTime, BigDecimal morningSupportPrice,
                          String lunchUseDays, String lunchDeliveryTime, BigDecimal lunchSupportPrice,
                          String dinnerUseDays, String dinnerDeliveryTime,BigDecimal dinnerSupportPrice);

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



}

