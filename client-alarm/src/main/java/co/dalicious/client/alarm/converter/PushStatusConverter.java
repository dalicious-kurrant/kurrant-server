package co.dalicious.client.alarm.converter;

import co.dalicious.client.alarm.entity.enums.PushStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PushStatusConverter implements AttributeConverter<PushStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PushStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public PushStatus convertToEntityAttribute(Integer dbData) {
        return PushStatus.ofCode(dbData);
    }
}
