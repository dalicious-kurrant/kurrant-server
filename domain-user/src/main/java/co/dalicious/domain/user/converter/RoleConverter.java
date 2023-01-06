package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RoleConverter implements AttributeConverter<Role, Long> {
    @Override
    public Long convertToDatabaseColumn(Role role) {
        return role.getCode();
    }

    @Override
    public Role convertToEntityAttribute(Long dbData) {
        return Role.ofCode(dbData);
    }
}
