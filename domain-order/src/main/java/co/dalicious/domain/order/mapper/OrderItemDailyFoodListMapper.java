package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.dto.OrderItemDto;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = OrderDailyFood.class)
public interface OrderItemDailyFoodListMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "dailyFood.id", target = "dailyFoodId")
    @Mapping(source = "dailyFood.food.name", target = "name")
    @Mapping(source = "orderStatus.code", target = "orderStatus")
    @Mapping(source = "dailyFood.food.makers.name", target = "makers")
    @Mapping(target = "image", expression = "java(orderItemDailyFood.getDailyFood().getFood().getImages() == null ? null : orderItemDailyFood.getDailyFood().getFood().getImages().get(0).getLocation())")
    @Mapping(source = "count", target = "count")
    OrderItemDto toDto(OrderItemDailyFood orderItemDailyFood);

}
