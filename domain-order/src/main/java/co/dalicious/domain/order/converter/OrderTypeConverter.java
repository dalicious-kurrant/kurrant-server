package co.dalicious.domain.order.converter;

import co.dalicious.domain.order.entity.enums.OrderType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class OrderTypeConverter implements AttributeConverter<OrderType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OrderType orderType) {
        return orderType.getCode();
    }

    @Override
    public OrderType convertToEntityAttribute(Integer dbData) {
        return OrderType.ofCode(dbData);
    }
}
