package co.dalicious.domain.board.converter;

import co.dalicious.domain.board.entity.enums.AlarmType;
import co.dalicious.system.util.DiningType;

import javax.persistence.AttributeConverter;

public class AlarmTypeConverter implements AttributeConverter<AlarmType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AlarmType alarmType) {
        return alarmType.getCode();
    }

    @Override
    public AlarmType convertToEntityAttribute(Integer dbData) {
        return AlarmType.ofCode(dbData);
    }

}
