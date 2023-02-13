package co.dalicious.domain.food.converter;

import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.entity.enums.FoodStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class FoodStatusConverter implements AttributeConverter<FoodStatus,Integer> {
    @Override
    public Integer convertToDatabaseColumn(FoodStatus dailyFoodStatus) {
        return dailyFoodStatus.getCode();
    }

    @Override
    public FoodStatus convertToEntityAttribute(Integer dbData) {
        return FoodStatus.ofCode(dbData);
    }
}
