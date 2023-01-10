package co.kurrant.app.public_api.mapper.order;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.entity.OrderCart;
import co.dalicious.domain.order.entity.OrderCartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface OrderCartMapper extends GenericMapper<OrderCartDto, OrderCart> {
    OrderCartDto toDto(OrderCart orderCart);
    OrderCart toEntity(OrderCartDto orderCartDto);

    @Mapping(source = "userId", target = "userId")
    OrderCart CreateOrderCart(BigInteger userId);

    @Mapping(source="id", target = "userId")
    OrderCart newOrderCart(BigInteger id);


}
