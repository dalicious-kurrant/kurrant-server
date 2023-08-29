package co.dalicious.domain.board.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Converter
public class GroupIdListConverter implements AttributeConverter<List<BigInteger>, String> {

    private static final String SEPARATOR = ",";
    @Override
    public String convertToDatabaseColumn(List<BigInteger> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (BigInteger id : attribute) {
            sb.append(id).append(SEPARATOR);
        }

        // remove the last separator
        sb.setLength(sb.length() - SEPARATOR.length());

        return sb.toString();
    }

    @Override
    public List<BigInteger> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] idStr = dbData.split(SEPARATOR);
        List<BigInteger> idList = new ArrayList<>();

        for(String id : idStr) {
            idList.add(BigInteger.valueOf(Integer.parseInt(id)));
        }
        return idList;
    }
}
