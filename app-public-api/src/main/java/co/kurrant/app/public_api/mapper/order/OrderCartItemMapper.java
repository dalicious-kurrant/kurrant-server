package co.kurrant.app.public_api.mapper.order;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.dto.CartItemDto;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.entity.OrderCart;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.system.util.DiningType;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderCartItemMapper extends GenericMapper<OrderCartDto, OrderCartItem> {


    @Mapping(source = "dailyFood", target = "dailyFood", qualifiedByName = "dailyFood")
    @Mapping(source = "orderCart", target = "orderCart", qualifiedByName = "orderCart")
    OrderCartItem toOrderCartItem(DailyFood dailyFood, OrderCart orderCart);

    @Named("dailyFood")
    default DailyFood dailyFood(DailyFood dailyFood){
        return DailyFood.builder()
                .id(dailyFood.getId())
                .serviceDate(dailyFood.getServiceDate())
                .status(dailyFood.getStatus())
                .food(dailyFood.getFood())
                .created(dailyFood.getCreated())
                .updated(dailyFood.getUpdated())
                .spotId(dailyFood.getSpotId())
                .diningType(dailyFood.getDiningType())
                .isSoldOut(dailyFood.getIsSoldOut())
                .build();
    }

    @Named("orderCart")
    default OrderCart orderCart(OrderCart orderCart){
        return OrderCart.builder()
                .id(orderCart.getId())
                .userId(orderCart.getUserId())
                .build();
    }

    @Mapping(source = "dailyFood", target="dailyFood")
    OrderCartItem CreateOrderCartItem(DailyFood dailyFood, Integer count, OrderCart orderCart);

    @Mapping(source = "orderCartItem", target = "orderCartItem", qualifiedByName = "orderCartItem")
    @Mapping(source = "price", target = "price", qualifiedByName = "price")
    CartItemDto toCartItemDto(OrderCartItem orderCartItem, Integer price, BigDecimal supportPrice,
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
