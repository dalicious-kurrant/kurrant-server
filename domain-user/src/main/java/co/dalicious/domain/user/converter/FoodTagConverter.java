package co.dalicious.domain.user.converter;

import co.dalicious.system.enums.FoodTag;

import javax.persistence.AttributeConverter;

public class FoodTagConverter implements AttributeConverter<FoodTag, String> {

    @Override
    public String convertToDatabaseColumn(FoodTag attribute) {
        return attribute.toString();
    }

    @Override
    public FoodTag convertToEntityAttribute(String dbData) {
        return FoodTag.ofString(dbData);
    }
}
