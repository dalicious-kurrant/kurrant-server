package co.dalicious.domain.board.converter;

import co.dalicious.domain.board.entity.enums.AlarmBoardType;

import javax.persistence.AttributeConverter;

public class AlarmTypeConverter implements AttributeConverter<AlarmBoardType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AlarmBoardType alarmBoardType) {
        return alarmBoardType.getCode();
    }

    @Override
    public AlarmBoardType convertToEntityAttribute(Integer dbData) {
        return AlarmBoardType.ofCode(dbData);
    }

}
