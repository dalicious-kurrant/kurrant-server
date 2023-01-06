package co.dalicious.domain.order.converter;

import co.dalicious.domain.order.entity.enums.OrderStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class OrderStatusConverter implements AttributeConverter<OrderStatus, Long> {
    @Override
    public Long convertToDatabaseColumn(OrderStatus orderStatus) {
        return orderStatus.getCode();
    }

    @Override
    public OrderStatus convertToEntityAttribute(Long dbData) {
        return OrderStatus.ofCode(dbData);
    }
}
