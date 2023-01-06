package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.PaymentType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PaymentTypeConverter implements AttributeConverter<PaymentType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PaymentType paymentType) {
        return paymentType.getCode();
    }

    @Override
    public PaymentType convertToEntityAttribute(Integer dbData) {
        return PaymentType.ofCode(dbData);
    }
}
