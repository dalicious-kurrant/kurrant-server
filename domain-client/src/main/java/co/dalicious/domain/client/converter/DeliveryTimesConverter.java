package co.dalicious.domain.client.converter;

import co.dalicious.system.util.DateUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class DeliveryTimesConverter implements AttributeConverter<List<LocalTime>, String> {
    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<LocalTime> deliveryTimes) {
        if (deliveryTimes == null || deliveryTimes.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (LocalTime deliveryTime : deliveryTimes) {
            sb.append(deliveryTime).append(SEPARATOR);
        }

        // remove the last separator
        sb.setLength(sb.length() - SEPARATOR.length());

        return sb.toString();
    }

    @Override
    public List<LocalTime> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] deliveryTimeStr = dbData.split(SEPARATOR);
        List<LocalTime> deliveryTimeList = new ArrayList<>();

        for(String deliveryTime : deliveryTimeStr) {
            deliveryTimeList.add(DateUtils.stringToLocalTime(deliveryTime));
        }
        return deliveryTimeList;
    }
}
