package co.dalicious.domain.payment.converter;

import co.dalicious.domain.payment.entity.enums.PaymentCompany;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PaymentCompanyConverter implements AttributeConverter<PaymentCompany, String> {

    @Override
    public String convertToDatabaseColumn(PaymentCompany attribute) {
        return (attribute == null) ? null : attribute.getCode();
    }

    @Override
    public PaymentCompany convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : PaymentCompany.ofCode(dbData);
    }
}
