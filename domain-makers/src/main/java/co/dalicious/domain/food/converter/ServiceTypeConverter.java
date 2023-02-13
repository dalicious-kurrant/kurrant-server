package co.dalicious.domain.food.converter;


import co.dalicious.domain.food.entity.enums.ServiceType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ServiceTypeConverter implements AttributeConverter<ServiceType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ServiceType attribute) {
        return attribute.getCode();
    }

    @Override
    public ServiceType convertToEntityAttribute(Integer dbData) {
        return ServiceType.ofCode(dbData);
    }
}
