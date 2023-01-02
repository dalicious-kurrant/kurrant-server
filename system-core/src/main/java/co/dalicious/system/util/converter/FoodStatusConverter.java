package co.dalicious.system.util.converter;

import co.dalicious.system.util.FoodStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class FoodStatusConverter implements AttributeConverter<FoodStatus,Integer> {
    @Override
    public Integer convertToDatabaseColumn(FoodStatus foodStatus) {
        return foodStatus.getCode();
    }

    @Override
    public FoodStatus convertToEntityAttribute(Integer dbData) {
        return FoodStatus.ofCode(dbData);
    }
}
