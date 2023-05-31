package co.dalicious.integration.client.user.converter;

import co.dalicious.system.util.DateUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Converter
public class RegionIdsConverter implements AttributeConverter<List<BigInteger>, String> {
    @Override
    public String convertToDatabaseColumn(List<BigInteger> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (BigInteger id : attribute) {
            sb.append(id).append(",");
        }

        // remove the last separator
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    @Override
    public List<BigInteger> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] idStr = dbData.split(",");
        List<BigInteger> ids = new ArrayList<>();

        for(String id : idStr) {
            ids.add(BigInteger.valueOf(Integer.parseInt(id)));
        }
        return ids;
    }
}
