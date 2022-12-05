package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.GourmetType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class GourmetTypeConverter implements AttributeConverter<GourmetType, Long> {
    @Override
    public Long convertToDatabaseColumn(GourmetType gourmetType) {
        return gourmetType.getCode();
    }

    @Override
    public GourmetType convertToEntityAttribute(Long dbData) {
        return GourmetType.ofCode(dbData);
    }
}
