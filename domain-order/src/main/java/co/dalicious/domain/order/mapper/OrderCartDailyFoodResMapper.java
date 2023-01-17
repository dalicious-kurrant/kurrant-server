package co.dalicious.domain.order.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.dto.CartItemDto;
import co.dalicious.domain.order.entity.OrderCart;
import co.dalicious.domain.order.entity.OrderCartDailyFood;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface OrderCartDailyFoodResMapper {

    @Mapping(source = "dailyFood", target="dailyFood")
    OrderCartDailyFood CreateOrderCartItem(DailyFood dailyFood, Integer count, OrderCart orderCart);

    @Mapping(source = "orderCartDailyFood", target = "orderCartDailyFood", qualifiedByName = "orderCartDailyFood")
    @Mapping(source = "price", target = "price", qualifiedByName = "price")
    @Mapping(source = "id", target="id")
    CartItemDto toCartItemDto(BigInteger id, OrderCartDailyFood orderCartDailyFood, Integer price, BigDecimal supportPrice,
                              BigDecimal deliveryFee, BigDecimal membershipPrice, BigDecimal discountPrice,
                              BigDecimal periodDiscountPrice, BigDecimal discountRate);


    @Named("orderCartDailyFood")
    default OrderCartDailyFood orderCartItem(OrderCartDailyFood orderCartDailyFood){
        return OrderCartDailyFood.builder()
                .orderCart(orderCartDailyFood.getOrderCart())
                .dailyFood(orderCartDailyFood.getDailyFood())
                .count(orderCartDailyFood.getCount())
                .build();
    }

    @Named("price")
    default Integer price(Integer price){
        return Math.abs(price);
    }
}
