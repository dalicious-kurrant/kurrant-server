package co.kurrant.app.public_api.mapper.DailyReport;

import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.user.dto.OrderByDateAndDiningTypeResDto;
import co.kurrant.app.public_api.dto.order.OrderItemDailyFoodToDailyReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderItemDailyFoodDailyReportMapper {

    @Mapping(source = "location", target = "imageLocation")
    @Mapping(source = "orderItemDailyFood.dailyFood.diningType", target = "diningType")
    @Mapping(source = "orderItemDailyFood.dailyFood.serviceDate", target = "eatDate")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.calorie", target = "calorie", qualifiedByName = "nullToZero")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.protein", target = "protein", qualifiedByName = "nullToZero")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.fat", target = "fat", qualifiedByName = "nullToZero")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.carbohydrate", target = "carbohydrate", qualifiedByName = "nullToZero")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.name", target = "name")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.makers.name", target = "title")
    OrderItemDailyFoodToDailyReportDto toDailyReportDto(OrderItemDailyFood orderItemDailyFood, String location);

    @Mapping(source = "orderItemDailyFood.dailyFood.id", target = "dailyFoodId")
    @Mapping(source = "location", target = "imageLocation")
    @Mapping(source = "isDuplicated", target = "isDuplicated")
    @Mapping(source = "spotName", target = "spotName")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.name", target = "foodName")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.makers.name", target = "makersName")
    OrderByDateAndDiningTypeResDto toOrderByDateDto(OrderItemDailyFood orderItemDailyFood, String location, String spotName, Boolean isDuplicated);


    @Named("nullToZero")
    default Integer nullToZero(Integer isNull){
        return isNull == null? 0 : isNull;
    }
}