package co.dalicious.domain.logs.entity.converter;


import co.dalicious.domain.logs.entity.enums.LogType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LogTypeConverter implements AttributeConverter<LogType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(LogType attribute) {
        return attribute.getCode();
    }

    @Override
    public LogType convertToEntityAttribute(Integer dbData) {
        return LogType.ofCode(dbData);
    }
}
