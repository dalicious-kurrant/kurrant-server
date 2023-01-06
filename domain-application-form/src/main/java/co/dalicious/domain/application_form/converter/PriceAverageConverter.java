package co.dalicious.domain.application_form.converter;

import co.dalicious.domain.application_form.entity.enums.PriceAverage;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PriceAverageConverter implements AttributeConverter<PriceAverage, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PriceAverage attribute) {
        return attribute.getCode();
    }

    @Override
    public PriceAverage convertToEntityAttribute(Integer dbData) {
        return PriceAverage.ofCode(dbData);
    }
}
