package co.dalicious.domain.board.converter;

import co.dalicious.domain.board.entity.enums.BoardOption;

import javax.persistence.AttributeConverter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BoardOptionConverter implements AttributeConverter<List<BoardOption>, String> {

    private static final String SEPARATOR = ",";
    @Override
    public String convertToDatabaseColumn(List<BoardOption> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (BoardOption code : attribute) {
            sb.append(code.getCode()).append(SEPARATOR);
        }

        // remove the last separator
        sb.setLength(sb.length() - SEPARATOR.length());

        return sb.toString();
    }

    @Override
    public List<BoardOption> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] idStr = dbData.split(SEPARATOR);
        List<BoardOption> idList = new ArrayList<>();

        for(String id : idStr) {
            idList.add(BoardOption.ofCode(Integer.parseInt(id)));
        }
        return idList;
    }
}
