package co.kurrant.app.public_api.mapper.DailyReport;

import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.kurrant.app.public_api.dto.order.OrderItemDailyFoodToDailyReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemDailyFoodDailyReportMapper {

    @Mapping(source = "orderItemDailyFood.dailyFood.diningType", target = "diningType")
    @Mapping(source = "orderItemDailyFood.dailyFood.serviceDate", target = "eatDate")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.calorie", target = "calorie")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.protein", target = "protein")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.fat", target = "fat")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.carbohydrate", target = "carbohydrate")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.name", target = "name")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.makers.name", target = "title")
    OrderItemDailyFoodToDailyReportDto toDailyReportDto(OrderItemDailyFood orderItemDailyFood);
}
