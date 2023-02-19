package co.dalicious.domain.food.converter;

import co.dalicious.domain.food.entity.enums.ScheduleStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ScheduleStatusConverter implements AttributeConverter<ScheduleStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ScheduleStatus scheduleStatus) {
        return scheduleStatus.getCode();
    }

    @Override
    public ScheduleStatus convertToEntityAttribute(Integer dbData) {
        return ScheduleStatus.ofCode(dbData);
    }
}