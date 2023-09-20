package co.dalicious.domain.application_form.converter;

import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import co.dalicious.domain.application_form.entity.enums.RecommendProgressStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RecommendProgressStatusConverter implements AttributeConverter<RecommendProgressStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(RecommendProgressStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public RecommendProgressStatus convertToEntityAttribute(Integer dbData) {
        return RecommendProgressStatus.ofCode(dbData);
    }
}
