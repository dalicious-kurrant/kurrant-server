package co.kurrant.app.public_api.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.entity.OrderCart;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderCartMapper extends GenericMapper<OrderCartDto, OrderCart> {
    OrderCartMapper INSTANCE = Mappers.getMapper(OrderCartMapper.class);
}
