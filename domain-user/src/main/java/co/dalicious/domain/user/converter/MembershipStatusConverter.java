package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.MembershipStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MembershipStatusConverter implements AttributeConverter<MembershipStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(MembershipStatus membershipStatus) {
        return membershipStatus.getCode();
    }

    @Override
    public MembershipStatus convertToEntityAttribute(Integer dbData) {
        return MembershipStatus.ofCode(dbData);
    }
}
