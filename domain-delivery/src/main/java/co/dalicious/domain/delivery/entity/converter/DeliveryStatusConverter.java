package co.dalicious.domain.delivery.entity.converter;

import co.dalicious.domain.delivery.entity.enums.DeliveryStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class DeliveryStatusConverter implements AttributeConverter<DeliveryStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(DeliveryStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public DeliveryStatus convertToEntityAttribute(Integer dbData) {
        return DeliveryStatus.ofCode(dbData);
    }
}
