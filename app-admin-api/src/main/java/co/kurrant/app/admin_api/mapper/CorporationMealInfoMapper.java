package co.kurrant.app.admin_api.mapper;


import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.enums.DiningType;
import co.kurrant.app.admin_api.dto.GroupDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface CorporationMealInfoMapper {




    @Mapping(source = "supportPrice", target = "supportPrice")
    CorporationMealInfo toEntity(DiningType diningType, LocalTime deliveryTime, LocalTime lastOrderTime, String serviceDays, Spot spot, BigDecimal supportPrice);



}
