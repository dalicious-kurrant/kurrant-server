package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.PushCondition;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PushConditionConverter implements AttributeConverter<PushCondition, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PushCondition attribute) {
        return attribute.getCode();
    }

    @Override
    public PushCondition convertToEntityAttribute(Integer dbData) {
        return PushCondition.ofCode(dbData);
    }
}
