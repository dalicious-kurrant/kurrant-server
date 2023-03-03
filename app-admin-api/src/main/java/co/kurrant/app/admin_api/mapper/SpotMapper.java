package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.dto.SpotResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;

import exception.ApiException;
import exception.ExceptionEnum;
import org.apache.commons.math3.analysis.function.Add;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

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
    @Mapping(source = "diningTypeTemp", target = "diningType")
    @Mapping(source = "spot.createdDateTime", target = "createdDateTime", qualifiedByName = "createdTimeFormat")
    @Mapping(source = "spot.updatedDateTime", target = "updatedDateTime", qualifiedByName = "updatedTimeFormat")
    @Mapping(source = "spot.address.location", target = "location", qualifiedByName = "getLocation")
    @Mapping(source = "spot.address.zipCode", target = "zipCode")
    @Mapping(source = "spot.address.address2", target = "address2")
    @Mapping(source = "spot.address.address1", target = "address1")
    @Mapping(source = "spot.group.name", target = "groupName")
    @Mapping(source = "spot.group.id", target = "groupId")
    @Mapping(source = "spot.name", target = "spotName")
    @Mapping(source = "spot.id", target = "spotId")
    SpotResponseDto toDto(Spot spot, String diningTypeTemp,
                          String breakfastUseDays, String breakfastDeliveryTime, BigDecimal breakfastSupportPrice,
                          String lunchUseDays, String lunchDeliveryTime, BigDecimal lunchSupportPrice,
                          String dinnerUseDays, String dinnerDeliveryTime, BigDecimal dinnerSupportPrice, String lastOrderTime);


    default SpotResponseDto toDto(Spot spot) {
        SpotResponseDto spotResponseDto = new SpotResponseDto();
        boolean isCorporation = spot instanceof CorporationSpot;
        spotResponseDto.setSpotId(spot.getId());
        spotResponseDto.setSpotName(spot.getName());
        spotResponseDto.setGroupId(spot.getGroup().getId());
        spotResponseDto.setGroupName(spot.getGroup().getName());
        spotResponseDto.setZipCode(spot.getAddress().getZipCode());
        spotResponseDto.setAddress1(spot.getAddress().getAddress1());
        spotResponseDto.setAddress2(spot.getAddress().getAddress2());
        spotResponseDto.setLocation(getLocation(spot.getAddress().getLocation()));
        spotResponseDto.setBreakfastLastOrderTime(spot.getMealInfo(DiningType.MORNING) == null ? null : DateUtils.timeToString(spot.getMealInfo(DiningType.MORNING).getLastOrderTime()));
        spotResponseDto.setBreakfastDeliveryTime(spot.getMealInfo(DiningType.MORNING) == null ? null : DateUtils.timeToString(spot.getMealInfo(DiningType.MORNING).getDeliveryTime()));
        spotResponseDto.setBreakfastUseDays(spot.getMealInfo(DiningType.MORNING) == null ? null : spot.getMealInfo(DiningType.MORNING).getServiceDays());
        spotResponseDto.setBreakfastSupportPrice(spot.getMealInfo(DiningType.MORNING) == null ? null : (isCorporation) ? ((CorporationMealInfo) spot.getMealInfo(DiningType.MORNING)).getSupportPrice() : null);
        spotResponseDto.setLunchLastOrderTime(spot.getMealInfo(DiningType.LUNCH) == null ? null : DateUtils.timeToString(spot.getMealInfo(DiningType.LUNCH).getLastOrderTime()));
        spotResponseDto.setLunchDeliveryTime(spot.getMealInfo(DiningType.LUNCH) == null ? null : DateUtils.timeToString(spot.getMealInfo(DiningType.LUNCH).getDeliveryTime()));
        spotResponseDto.setLunchUseDays(spot.getMealInfo(DiningType.LUNCH) == null ? null : spot.getMealInfo(DiningType.LUNCH).getServiceDays());
        spotResponseDto.setLunchSupportPrice(spot.getMealInfo(DiningType.LUNCH) == null ? null : (isCorporation) ? ((CorporationMealInfo) spot.getMealInfo(DiningType.LUNCH)).getSupportPrice() : null);
        spotResponseDto.setDinnerLastOrderTime(spot.getMealInfo(DiningType.DINNER) == null ? null : DateUtils.timeToString(spot.getMealInfo(DiningType.DINNER).getLastOrderTime()));
        spotResponseDto.setDinnerDeliveryTime(spot.getMealInfo(DiningType.DINNER) == null ? null : DateUtils.timeToString(spot.getMealInfo(DiningType.DINNER).getDeliveryTime()));
        spotResponseDto.setDinnerUseDays(spot.getMealInfo(DiningType.DINNER) == null ? null : spot.getMealInfo(DiningType.DINNER).getServiceDays());
        spotResponseDto.setDinnerSupportPrice(spot.getMealInfo(DiningType.DINNER) == null ? null : (isCorporation) ? ((CorporationMealInfo) spot.getMealInfo(DiningType.DINNER)).getSupportPrice() : null);
        spotResponseDto.setCreatedDateTime(DateUtils.format(spot.getCreatedDateTime().toLocalDateTime().toLocalDate()));
        spotResponseDto.setUpdatedDateTime(DateUtils.format(spot.getUpdatedDateTime().toLocalDateTime().toLocalDate()));

        List<MealInfo> mealInfos = spot.getMealInfos();
        StringJoiner diningTypes = new StringJoiner(", ");
        for (MealInfo mealInfo : mealInfos) {
            diningTypes.add(mealInfo.getDiningType().getDiningType());
        }
        String diningTypesStr = diningTypes.toString();

        spotResponseDto.setDiningType(diningTypesStr);
        return spotResponseDto;
    }

    default MealInfo toMealInfo(Spot spot, DiningType diningType, String lastOrderTime, String deliveryTime, String useDays, BigDecimal supportPrice) {
        // MealInfo 를 생성하기 위한 기본값이 존재하지 않으면 객체 생성 X
        if (lastOrderTime == null || deliveryTime == null || useDays == null) {
            return null;
        }
        // 기업 스팟인 경우
        if (spot instanceof CorporationSpot) {
            return CorporationMealInfo.builder()
                    .spot(spot)
                    .diningType(diningType)
                    .lastOrderTime(DateUtils.stringToLocalTime(lastOrderTime))
                    .deliveryTime(DateUtils.stringToLocalTime(deliveryTime))
                    .serviceDays(useDays)
                    .supportPrice(supportPrice)
                    .build();
        } else if (spot instanceof ApartmentSpot) {
            return ApartmentMealInfo.builder()
                    .spot(spot)
                    .diningType(diningType)
                    .lastOrderTime(DateUtils.stringToLocalTime(lastOrderTime))
                    .deliveryTime(DateUtils.stringToLocalTime(deliveryTime))
                    .serviceDays(useDays)
                    .build();
        }
        return null;
    }

    @Named("createdTimeFormat")
    default String createdTimeFormat(Timestamp time) {
        return DateUtils.format(time, "yyyy-MM-dd");
    }

    @Named("updatedTimeFormat")
    default String updatedTimeFormat(Timestamp time) {
        return DateUtils.format(time, "yyyy-MM-dd");
    }

    @Named("getLocation")
    default String getLocation(Geometry location) {
        if (location == null) {
            return null;
        }
        return location.toString();
    }


    @Mapping(source = "address", target = "address")
    @Mapping(source = "spotInfo.groupId", target = "group", qualifiedByName = "generatedGroup")
    @Mapping(source = "spotInfo.spotName", target = "name")
    @Mapping(source = "diningTypes", target = "diningTypes")
    Spot toEntity(SpotResponseDto spotInfo, Address address, List<DiningType> diningTypes);


    default Spot toEntity(SpotResponseDto spotInfo, Group group, List<DiningType> diningTypes) throws ParseException {
        Set<DiningType> groupDiningTypes = new HashSet<>(group.getDiningTypes());
        if (!groupDiningTypes.containsAll(diningTypes)) {
            throw new ApiException(ExceptionEnum.GROUP_DOSE_NOT_HAVE_DINING_TYPE);
        }
        //TODO: Location 생성
        String location = spotInfo.getLocation();
        Address address = new Address(spotInfo.getZipCode(), spotInfo.getAddress1(), spotInfo.getAddress2(), location);
        if(group instanceof Apartment) return new ApartmentSpot(spotInfo.getSpotName(), address, diningTypes, group);
        if(group instanceof Corporation) return new CorporationSpot(spotInfo.getSpotName(), address, diningTypes, group);
        return null;
    }

    @Named("generatedGroup")
    default Group generatedGroup(BigInteger groupId) {
        return new Group(groupId);
    }

    @Mapping(source = "group.name", target = "name")
    @Mapping(source = "group.address", target = "address")
    @Mapping(source = "group.diningTypes", target = "diningTypes")
    @Mapping(source = "group", target = "group")
    Spot toEntity(Group group);
}

