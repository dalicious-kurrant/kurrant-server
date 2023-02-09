package co.dalicious.domain.food.converter;

import co.dalicious.domain.food.entity.enums.DailyFoodStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class DailyFoodStatusConverter implements AttributeConverter<DailyFoodStatus,Integer> {
    @Override
    public Integer convertToDatabaseColumn(DailyFoodStatus dailyFoodStatus) {
        return dailyFoodStatus.getCode();
    }

    @Override
    public DailyFoodStatus convertToEntityAttribute(Integer dbData) {
        return DailyFoodStatus.ofCode(dbData);
    }
}
