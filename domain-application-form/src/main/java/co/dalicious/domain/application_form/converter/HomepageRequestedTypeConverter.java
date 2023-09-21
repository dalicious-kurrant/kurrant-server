package co.dalicious.domain.application_form.converter;

import co.dalicious.domain.application_form.entity.enums.HomepageRequestedType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class HomepageRequestedTypeConverter implements AttributeConverter<HomepageRequestedType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(HomepageRequestedType attribute) {
        return attribute.getCode();
    }

    @Override
    public HomepageRequestedType convertToEntityAttribute(Integer dbData) {
        return HomepageRequestedType.ofCode(dbData);
    }
}
