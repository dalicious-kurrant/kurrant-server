package co.dalicious.domain.makers.converter;

import co.dalicious.domain.makers.entity.enums.ServiceForm;
import co.dalicious.domain.makers.entity.enums.ServiceType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ServiceFormConverter implements AttributeConverter<ServiceForm, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ServiceForm attribute) {
        return attribute.getCode();
    }

    @Override
    public ServiceForm convertToEntityAttribute(Integer dbData) {
        return ServiceForm.ofCode(dbData);
    }
}
