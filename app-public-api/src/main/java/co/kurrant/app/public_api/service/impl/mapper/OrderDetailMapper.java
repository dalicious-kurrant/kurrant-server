package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.user.dto.OrderItemDto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper extends GenericMapper<OrderItemDto, OrderItem> {
    OrderDetailMapper INSTANCE = Mappers.getMapper(OrderDetailMapper.class);
}
