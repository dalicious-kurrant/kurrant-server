package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MembershipSubscriptionTypeConverter implements AttributeConverter<MembershipSubscriptionType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(MembershipSubscriptionType membershipSubscriptionType) {
        return membershipSubscriptionType.getCode();
    }

    @Override
    public MembershipSubscriptionType convertToEntityAttribute(Integer dbData) {
        return MembershipSubscriptionType.ofCode(dbData);
    }
}
