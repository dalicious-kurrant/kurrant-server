package co.dalicious.domain.payment.converter;

import co.dalicious.domain.payment.entity.enums.PointCondition;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PointConditionConverter implements AttributeConverter<PointCondition, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PointCondition condition) {
        return (condition == null) ? null : condition.getCode();
    }

    @Override
    public PointCondition convertToEntityAttribute(Integer dbData) {
        return (dbData == null) ? null : PointCondition.ofCode(dbData);
    }
}
