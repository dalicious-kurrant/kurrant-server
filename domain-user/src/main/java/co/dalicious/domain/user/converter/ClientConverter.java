package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.ClientType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ClientConverter implements AttributeConverter<ClientType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ClientType attribute) {
        return attribute.getCode();
    }

    @Override
    public ClientType convertToEntityAttribute(Integer dbData) {
        return ClientType.ofCode(dbData);
    }
}
