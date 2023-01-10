package co.kurrant.app.public_api.mapper.order;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.entity.OrderCart;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.system.util.DiningType;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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
}
