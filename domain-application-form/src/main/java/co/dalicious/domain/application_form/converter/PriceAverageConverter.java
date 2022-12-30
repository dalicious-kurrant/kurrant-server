package co.dalicious.domain.application_form.converter;

import co.dalicious.domain.application_form.entity.PriceAverage;

import javax.persistence.AttributeConverter;

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
