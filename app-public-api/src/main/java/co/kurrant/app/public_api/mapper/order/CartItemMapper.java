package co.kurrant.app.public_api.mapper.order;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.order.dto.CartDailyFoodDto;
import co.dalicious.domain.order.entity.CartDailyFood;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper extends GenericMapper<CartDailyFoodDto, CartDailyFood> {
//
//    CartDailyFoodDto toDto(CartDailyFood orderCartDailyFood);
//
//    CartDailyFood toEntity(CartDailyFoodDto cartItemDto);
}
