package co.dalicious.domain.application_form.converter;

import co.dalicious.domain.application_form.entity.ProgressStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ProgressStausConverter implements AttributeConverter<ProgressStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProgressStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public ProgressStatus convertToEntityAttribute(Integer dbData) {
        return ProgressStatus.ofCode(dbData);
    }
}
