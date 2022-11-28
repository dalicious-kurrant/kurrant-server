package co.dalicious.system.util.converter;

import co.dalicious.system.util.DiningType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class DiningTypeConverter implements AttributeConverter<DiningType, Long> {

    @Override
    public Long convertToDatabaseColumn(DiningType diningType) {
        return diningType.getCode();
    }

    @Override
    public DiningType convertToEntityAttribute(Long dbData) {
        return DiningType.ofCode(dbData);
    }
}
