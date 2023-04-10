package co.dalicious.domain.client.converter;

import co.dalicious.domain.client.entity.enums.SparkPlusLogType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SparkPlusLogTypeConverter implements AttributeConverter<SparkPlusLogType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(SparkPlusLogType attribute) {
        return attribute.getCode();
    }

    @Override
    public SparkPlusLogType convertToEntityAttribute(Integer dbData) {
        return SparkPlusLogType.ofCode(dbData);
    }
}
