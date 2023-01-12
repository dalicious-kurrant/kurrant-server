package co.kurrant.app.public_api.mapper.order;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.order.dto.CartItemDto;
import co.dalicious.domain.order.entity.OrderCartItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CartItemMapper extends GenericMapper<CartItemDto, OrderCartItem> {

    CartItemDto toDto(OrderCartItem orderCartItem);

    OrderCartItem toEntity(CartItemDto cartItemDto);
}