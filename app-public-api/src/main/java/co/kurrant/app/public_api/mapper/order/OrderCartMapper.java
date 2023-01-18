package co.kurrant.app.public_api.mapper.order;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.order.dto.CartDto;
import co.dalicious.domain.order.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface OrderCartMapper extends GenericMapper<CartDto, Cart> {
    CartDto toDto(Cart cart);
    Cart toEntity(CartDto cartDto);

    @Mapping(source = "userId", target = "userId")
    Cart CreateOrderCart(BigInteger userId);

    @Mapping(source="id", target = "userId")
    Cart newOrderCart(BigInteger id);


}
