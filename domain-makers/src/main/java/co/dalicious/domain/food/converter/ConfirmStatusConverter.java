package co.dalicious.domain.food.converter;

import co.dalicious.domain.food.entity.enums.ConfirmStatus;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ConfirmStatusConverter implements AttributeConverter<ConfirmStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ConfirmStatus confirmStatus) {
        return confirmStatus.getCode();
    }

    @Override
    public ConfirmStatus convertToEntityAttribute(Integer dbData) {
        return ConfirmStatus.ofCode(dbData);
    }
}
