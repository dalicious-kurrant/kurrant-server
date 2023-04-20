package co.dalicious.system.converter;

import co.dalicious.system.enums.DiscountType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


@Converter
public class DiscountTypeConverter implements AttributeConverter<DiscountType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(DiscountType attribute) {
        return attribute.getCode();
    }

    @Override
    public DiscountType convertToEntityAttribute(Integer dbData) {
        return DiscountType.ofCode(dbData);
    }
}
