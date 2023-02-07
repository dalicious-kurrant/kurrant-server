package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.dto.CartDailyFoodDto;
import co.dalicious.domain.order.dto.DiningTypeServiceDateDto;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFoodGroup;
import co.dalicious.system.util.enums.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", imports = DiningType.class)
public interface OrderDailyFoodItemMapper {
    @Mapping(target = "orderStatus", constant = "PENDING_PAYMENT")
    @Mapping(source = "order", target = "order")
    @Mapping(source = "cartDailyFood.dailyFood", target = "dailyFood")
    @Mapping(source = "cartDailyFood.dailyFood.food.name", target = "name")
    @Mapping(source = "cartDailyFood.dailyFood.food.price", target = "price")
    @Mapping(source = "cartDailyFoodDto.discountedPrice", target = "discountedPrice")
    @Mapping(source = "cartDailyFood.count", target = "count")
    @Mapping(source = "cartDailyFoodDto.membershipDiscountRate", target = "membershipDiscountRate")
    @Mapping(source = "cartDailyFoodDto.makersDiscountRate", target = "makersDiscountRate")
    @Mapping(source = "cartDailyFoodDto.periodDiscountRate", target = "periodDiscountRate")
    @Mapping(source = "orderItemDailyFoodGroup", target = "orderItemDailyFoodGroup")
    OrderItemDailyFood dtoToOrderItemDailyFood(CartDailyFoodDto.DailyFood cartDailyFoodDto, CartDailyFood cartDailyFood, Order order, OrderItemDailyFoodGroup orderItemDailyFoodGroup);


    @Mapping(target = "orderStatus", constant = "PENDING_PAYMENT")
    @Mapping(target = "serviceDate", expression = "java(DateUtils.format(cartDailyFoodDto.getServiceDate()))")
    @Mapping(target = "diningType", expression = "java(DiningType.ofString(cartDailyFoodDto.getDiningType()))")
    @Mapping(source = "deliveryFee", target = "deliveryFee")
    OrderItemDailyFoodGroup dtoToOrderItemDailyFoodGroup(CartDailyFoodDto cartDailyFoodDto);
}
