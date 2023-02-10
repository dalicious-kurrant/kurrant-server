package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.UserStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class UserStatusConverter implements AttributeConverter<UserStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserStatus userStatus) {
        return userStatus.getCode();
    }

    @Override
    public UserStatus convertToEntityAttribute(Integer dbData) {
        return UserStatus.ofCode(dbData);
    }
}
