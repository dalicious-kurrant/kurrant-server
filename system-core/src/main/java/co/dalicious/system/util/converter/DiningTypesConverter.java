package co.dalicious.system.util.converter;

import co.dalicious.system.util.DiningType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class DiningTypesConverter implements AttributeConverter<List<DiningType>, String> {
    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<DiningType> diningTypes) {
        if (diningTypes == null || diningTypes.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (DiningType diningType : diningTypes) {
            sb.append(diningType.getCode()).append(SEPARATOR);
        }

        // remove the last separator
        sb.setLength(sb.length() - SEPARATOR.length());

        return sb.toString();
    }

    @Override
    public List<DiningType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] diningTypeStrings = dbData.split(SEPARATOR);
        List<DiningType> diningTypes = new ArrayList<>();

        for(String diningTypeString : diningTypeStrings) {
            diningTypes.add(DiningType.ofCode(Integer.parseInt(diningTypeString)));
        }
        return diningTypes;
    }
}
