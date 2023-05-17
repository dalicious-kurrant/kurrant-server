package co.dalicious.client.core.converter;


import co.dalicious.client.core.enums.ControllerType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ControllerTypeConverter implements AttributeConverter<ControllerType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ControllerType attribute) {
        return attribute.getCode();
    }

    @Override
    public ControllerType convertToEntityAttribute(Integer dbData) {
        return ControllerType.ofCode(dbData);
    }
}
