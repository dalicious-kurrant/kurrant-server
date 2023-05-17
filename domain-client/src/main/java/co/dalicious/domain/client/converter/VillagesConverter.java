package co.dalicious.domain.client.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class VillagesConverter implements AttributeConverter<List<String>, String> {
    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> villages) {
        if (villages == null || villages.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (String zipcode : villages) {
            sb.append(zipcode).append(SEPARATOR);
        }

        // remove the last separator
        sb.setLength(sb.length() - SEPARATOR.length());

        return sb.toString();
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] villagesStr = dbData.split(SEPARATOR);

        return new ArrayList<>(Arrays.asList(villagesStr));
    }
}
