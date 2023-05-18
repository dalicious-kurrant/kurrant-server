package co.dalicious.system.converter;

import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Converter
public class DaysListConverter implements AttributeConverter<List<Days>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<Days> days) {
        if (days == null || days.isEmpty()) {
            return null;
        }
        Collections.sort(days);
        StringBuilder sb = new StringBuilder();
        for (Days day : days) {
            sb.append(day.getCode()).append(SEPARATOR);
        }

        // remove the last separator
        sb.setLength(sb.length() - SEPARATOR.length());

        return sb.toString();
    }

    @Override
    public List<Days> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] daysString = dbData.split(SEPARATOR);
        List<Days> days = new ArrayList<>();

        for(String dayString : daysString) {
            days.add(Days.ofCode(Integer.parseInt(dayString)));
        }
        return days;
    }
}
