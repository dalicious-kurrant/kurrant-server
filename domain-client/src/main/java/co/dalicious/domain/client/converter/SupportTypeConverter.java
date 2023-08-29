package co.dalicious.domain.client.converter;

import co.dalicious.domain.client.entity.enums.SupportType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SupportTypeConverter implements AttributeConverter<SupportType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(SupportType attribute) {
        return attribute.getCode();
    }

    @Override
    public SupportType convertToEntityAttribute(Integer dbData) {
        return SupportType.ofCode(dbData);
    }
}
