package co.dalicious.domain.order.converter;

import co.dalicious.domain.order.entity.enums.OrderType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class OrderTypeConverter implements AttributeConverter<OrderType, Long> {
    @Override
    public Long convertToDatabaseColumn(OrderType orderType) {
        return orderType.getCode();
    }

    @Override
    public OrderType convertToEntityAttribute(Long dbData) {
        return OrderType.ofCode(dbData);
    }
}
