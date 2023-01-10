package co.kurrant.app.public_api.mapper.order;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.dto.OrderItemDto;
import co.dalicious.domain.order.entity.OrderItem;

import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper extends GenericMapper<OrderItemDto, OrderItem> {
    OrderItemDto toDto(OrderItem orderItem);
    OrderItem toEntity(OrderItemDto orderItemDto);


    @Mapping(source = "food", target = "food", qualifiedByName = "food")
    @Mapping(source = "orderItem", target = "orderItem", qualifiedByName = "orderItem")
    OrderItemDto toOrderItemDto(Food food, OrderItem orderItem);

    @Named("food")
    default Food food(Food food){return food;}

    @Named("orderItem")
    default OrderItem orderItem(OrderItem orderItem){ return orderItem;}

}
