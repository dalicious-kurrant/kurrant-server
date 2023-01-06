package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.GourmetType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class GourmetTypeConverter implements AttributeConverter<GourmetType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(GourmetType gourmetType) {
        return gourmetType.getCode();
    }

    @Override
    public GourmetType convertToEntityAttribute(Integer dbData) {
        return GourmetType.ofCode(dbData);
    }
}
