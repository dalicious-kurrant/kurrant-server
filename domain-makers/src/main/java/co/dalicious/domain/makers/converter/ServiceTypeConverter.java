package co.dalicious.domain.makers.converter;

import co.dalicious.domain.makers.entity.enums.ServiceType;

import javax.persistence.AttributeConverter;

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
