package co.kurrant.app.admin_api.mapper;


import co.dalicious.domain.client.entity.ApartmentMealInfo;
import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalTime;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface CorporationMealInfoMapper {

    @Mapping(source = "supportPrice", target = "supportPrice")
    CorporationMealInfo toEntity(DiningType diningType, LocalTime deliveryTime, LocalTime lastOrderTime, String serviceDays, Spot spot, BigDecimal supportPrice);

    @Mapping(source = "diningType", target = "diningType")
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.stringToLocalTime(defaultTime))")
    @Mapping(target = "lastOrderTime", expression = "java(DateUtils.stringToLocalTime(defaultTime))")
    @Mapping(source = "groupInfoList.serviceDays", target = "serviceDays")
    @Mapping(source = "spot", target = "spot")
    CorporationMealInfo toCorporationMealInfoEntity(GroupExcelRequestDto groupInfoList, Spot spot, DiningType diningType, String defaultTime);

    @Mapping(source = "diningType", target = "diningType")
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.stringToLocalTime(defaultTime))")
    @Mapping(target = "lastOrderTime", expression = "java(DateUtils.stringToLocalTime(defaultTime))")
    @Mapping(source = "groupInfoList.serviceDays", target = "serviceDays")
    @Mapping(source = "spot", target = "spot")
    ApartmentMealInfo toApartmentMealInfoEntity(GroupExcelRequestDto groupInfoList, Spot spot, DiningType diningType, String defaultTime);
}
