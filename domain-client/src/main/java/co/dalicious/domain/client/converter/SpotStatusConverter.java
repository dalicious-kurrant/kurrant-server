package co.dalicious.domain.client.converter;

import co.dalicious.domain.client.entity.enums.SpotStatus;

import javax.persistence.AttributeConverter;

public class SpotStatusConverter implements AttributeConverter<SpotStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(SpotStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public SpotStatus convertToEntityAttribute(Integer dbData) {
        return SpotStatus.ofCode(dbData);
    }
}
