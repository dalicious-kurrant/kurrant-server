package co.dalicious.domain.client.converter;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.system.util.DateUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalTime;

@Converter
public class DayAndTimeConverter implements AttributeConverter<DayAndTime, String> {
    @Override
    public String convertToDatabaseColumn(DayAndTime dayAndTime) {
        if (dayAndTime == null) {
            return null;
        }
        String day = Integer.toString(dayAndTime.getDay() == null ? 0 : dayAndTime.getDay());
        String time = DateUtils.timeToString(dayAndTime.getTime());
        return String.format("%s (%s)", day, time);
    }

    @Override
    public DayAndTime convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        String[] parts = dbValue.split(" \\(");
        int day = Integer.parseInt(parts[0]);
        LocalTime time = DateUtils.stringToLocalTime(parts[1].substring(0, parts[1].length() - 1));
        return new DayAndTime(day, time);
    }
}
