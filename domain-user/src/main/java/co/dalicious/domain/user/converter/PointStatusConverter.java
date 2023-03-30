package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.PointStatus;

import javax.persistence.AttributeConverter;

public class PointStatusConverter implements AttributeConverter<PointStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PointStatus attribute) {
        return (attribute == null) ? null : attribute.getCode();
    }

    @Override
    public PointStatus convertToEntityAttribute(Integer dbData) {
        return (dbData == null) ? null : PointStatus.ofCode(dbData);
    }
}
