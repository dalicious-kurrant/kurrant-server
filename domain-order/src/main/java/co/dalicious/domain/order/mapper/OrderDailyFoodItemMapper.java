package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.dto.CartDailyFoodDto;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDailyFoodItemMapper {
    @Mapping(target = "orderStatus", constant = "PENDING_PAYMENT")
    @Mapping(source = "order", target = "order")
    @Mapping(source = "cartDailyFood.dailyFood.serviceDate", target = "serviceDate")
    @Mapping(source = "cartDailyFood.dailyFood.diningType", target = "diningType")
    @Mapping(source = "cartDailyFood.dailyFood.food", target = "food")
    @Mapping(source = "cartDailyFood.dailyFood.food.name", target = "name")
    @Mapping(source = "cartDailyFood.dailyFood.food.price", target = "price")
    @Mapping(source = "cartDailyFoodDto.discountedPrice", target = "discountedPrice")
    @Mapping(source = "cartDailyFood.count", target = "count")
    @Mapping(source = "cartDailyFoodDto.membershipDiscountRate", target = "membershipDiscountRate")
    @Mapping(source = "cartDailyFoodDto.makersDiscountRate", target = "makersDiscountRate")
    @Mapping(source = "cartDailyFoodDto.periodDiscountRate", target = "periodDiscountRate")
    OrderItemDailyFood toEntity(CartDailyFoodDto.DailyFood cartDailyFoodDto, CartDailyFood cartDailyFood, Order order);
}
