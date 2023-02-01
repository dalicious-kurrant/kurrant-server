package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.dto.OrderItemDto;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = OrderDailyFood.class)
public interface OrderItemDailyFoodListMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "id", target = "dailyFoodId")
    @Mapping(source = "food.name", target = "name")
    @Mapping(source = "orderStatus.code", target = "orderStatus")
    @Mapping(source = "food.makers.name", target = "makers")
    @Mapping(source = "food.image.location", target = "image")
    @Mapping(source = "count", target = "count")
    OrderItemDto toDto(OrderItemDailyFood orderItemDailyFood);

}
