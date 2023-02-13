package co.dalicious.domain.food.converter;

import co.dalicious.domain.food.entity.enums.ServiceForm;

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
