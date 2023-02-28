package co.kurrant.app.admin_api.mapper;


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

    @Mapping(target = "diningType", expression = "java(spot.getDiningTypes().get(0))")
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.stringToLocalTime(defaultTime))")
    @Mapping(target = "lastOrderTime", expression = "java(DateUtils.stringToLocalTime(defaultTime))")
    @Mapping(source = "groupInfoList.serviceDays", target = "serviceDays")
    @Mapping(source = "spot", target = "spot")
    MealInfo toEntity(GroupExcelRequestDto groupInfoList, Spot spot, String defaultTime);
}
