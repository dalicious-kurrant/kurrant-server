package co.dalicious.domain.client.converter;

import co.dalicious.domain.client.entity.enums.DeliveryFeeOption;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class DeliveryFeeOptionConverter implements AttributeConverter<DeliveryFeeOption, Integer> {
    @Override
    public Integer convertToDatabaseColumn(DeliveryFeeOption deliveryFeeOption) {
       return deliveryFeeOption.getCode();
    }

    @Override
    public DeliveryFeeOption convertToEntityAttribute(Integer dbValue) {
        return DeliveryFeeOption.ofCode(dbValue);
    }
}
