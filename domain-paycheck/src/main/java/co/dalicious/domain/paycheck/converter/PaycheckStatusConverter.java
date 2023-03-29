package co.dalicious.domain.paycheck.converter;

import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PaycheckStatusConverter implements AttributeConverter<PaycheckStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PaycheckStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public PaycheckStatus convertToEntityAttribute(Integer dbData) {
        return PaycheckStatus.ofCode(dbData);
    }
}
