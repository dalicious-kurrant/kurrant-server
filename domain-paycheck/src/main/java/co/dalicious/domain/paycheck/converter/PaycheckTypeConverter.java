package co.dalicious.domain.paycheck.converter;

import co.dalicious.domain.paycheck.entity.enums.PaycheckType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PaycheckTypeConverter implements AttributeConverter<PaycheckType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PaycheckType attribute) {
        return attribute.getCode();
    }

    @Override
    public PaycheckType convertToEntityAttribute(Integer dbData) {
        return PaycheckType.ofCode(dbData);
    }
}
