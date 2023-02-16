package co.dalicious.system.converter;

import co.dalicious.system.enums.DiningType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class DiningTypeConverter implements AttributeConverter<DiningType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(DiningType attribute) {
        return attribute.getCode();
    }

    @Override
    public DiningType convertToEntityAttribute(Integer dbData) {
        return DiningType.ofCode(dbData);
    }
}
