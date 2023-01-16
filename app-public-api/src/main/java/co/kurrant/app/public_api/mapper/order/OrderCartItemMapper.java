package co.kurrant.app.public_api.mapper.order;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.dto.CartItemDto;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.entity.OrderCart;
import co.dalicious.domain.order.entity.OrderCartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface OrderCartItemMapper extends GenericMapper<OrderCartDto, OrderCartItem> {

    @Mapping(source = "dailyFood", target="dailyFood")
    OrderCartItem CreateOrderCartItem(DailyFood dailyFood, Integer count, OrderCart orderCart);

    @Mapping(source = "orderCartItem", target = "orderCartItem", qualifiedByName = "orderCartItem")
    @Mapping(source = "price", target = "price", qualifiedByName = "price")
    @Mapping(source = "id", target="id")
    CartItemDto toCartItemDto(BigInteger id, OrderCartItem orderCartItem, Integer price, BigDecimal supportPrice,
                              BigDecimal deliveryFee, BigDecimal membershipPrice, BigDecimal discountPrice,
                              BigDecimal periodDiscountPrice, BigDecimal discountRate);


    @Named("orderCartItem")
    default OrderCartItem orderCartItem(OrderCartItem orderCartItem){
        return OrderCartItem.builder()
                .orderCart(orderCartItem.getOrderCart())
                .dailyFood(orderCartItem.getDailyFood())
                .count(orderCartItem.getCount())
                .build();
    }

    @Named("price")
    default Integer price(Integer price){
        return Math.abs(price);
    }
}
