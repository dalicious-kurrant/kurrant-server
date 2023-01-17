package co.kurrant.app.public_api.mapper.order;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.order.dto.CartItemDto;
import co.dalicious.domain.order.entity.OrderCartDailyFood;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper extends GenericMapper<CartItemDto, OrderCartDailyFood> {

    CartItemDto toDto(OrderCartDailyFood orderCartDailyFood);

    OrderCartDailyFood toEntity(CartItemDto cartItemDto);
}
