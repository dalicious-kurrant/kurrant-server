package co.dalicious.domain.board.converter;

import co.dalicious.domain.board.entity.enums.BoardCategory;
import co.dalicious.domain.board.entity.enums.BoardType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BoardCategoryConverter implements AttributeConverter<BoardCategory, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BoardCategory attribute) {
        return attribute.getCode();
    }

    @Override
    public BoardCategory convertToEntityAttribute(Integer dbData) {
        return BoardCategory.ofCode(dbData);
    }
}
