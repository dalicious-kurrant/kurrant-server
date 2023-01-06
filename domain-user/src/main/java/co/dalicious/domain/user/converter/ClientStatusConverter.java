package co.dalicious.domain.user.converter;


import co.dalicious.domain.user.entity.enums.ClientStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ClientStatusConverter implements AttributeConverter<ClientStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ClientStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public ClientStatus convertToEntityAttribute(Integer dbData) {
        return ClientStatus.ofCode(dbData);
    }
}
