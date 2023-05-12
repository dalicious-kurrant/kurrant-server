package co.dalicious.domain.paycheck.converter;

import co.dalicious.domain.paycheck.entity.enums.PaycheckCategoryItem;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PaycheckCategoryItemConverter implements AttributeConverter<PaycheckCategoryItem, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PaycheckCategoryItem attribute) {
        return attribute.getCode();
    }

    @Override
    public PaycheckCategoryItem convertToEntityAttribute(Integer dbData) {
        return PaycheckCategoryItem.ofCode(dbData);
    }
}
