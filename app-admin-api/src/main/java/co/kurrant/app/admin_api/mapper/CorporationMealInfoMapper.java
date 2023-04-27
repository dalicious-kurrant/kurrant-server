//package co.kurrant.app.admin_api.mapper;
//
//
//import co.dalicious.domain.client.entity.*;
//import co.dalicious.system.enums.DiningType;
//import co.dalicious.system.util.DateUtils;
//import co.dalicious.domain.client.dto.GroupExcelRequestDto;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.Named;
//
//import java.math.BigDecimal;
//import java.time.LocalTime;
//
//@Mapper(componentModel = "spring", imports = {DateUtils.class, DayAndTime.class, MealInfo.class})
//public interface CorporationMealInfoMapper {
//
//    @Mapping(source = "supportPrice", target = "supportPrice")
//    CorporationMealInfo toEntity(DiningType diningType, LocalTime deliveryTime, DayAndTime lastOrderTime, String serviceDays, Spot spot, BigDecimal supportPrice);
//
//}
