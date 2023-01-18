package co.dalicious.system.util.converter;

import co.dalicious.system.util.enums.DiningType;
import co.dalicious.system.util.enums.DiscountType;

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
