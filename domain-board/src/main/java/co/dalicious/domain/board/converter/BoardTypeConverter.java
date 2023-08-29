package co.dalicious.domain.board.converter;

import co.dalicious.domain.board.entity.enums.BoardType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BoardTypeConverter implements AttributeConverter<BoardType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BoardType attribute) {
        return attribute.getCode();
    }

    @Override
    public BoardType convertToEntityAttribute(Integer dbData) {
        return BoardType.ofCode(dbData);
    }
}
