package co.dalicious.system.util;

import co.dalicious.system.enums.Days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DaysUtil {
    public static String serviceDaysToDbData(List<Integer> days) {
        Collections.sort(days);
        StringBuilder serviceDays = new StringBuilder();
        for(Integer day : days) {
            serviceDays.append(day.toString()).append(", ");
        }
        return serviceDays.substring(0, serviceDays.length() - 2);
    }

    public static String serviceDaysToString(String dbData) {
        StringBuilder serviceDays = new StringBuilder();
        String[] parts = dbData.split(", ");
        for (String part : parts) {
            String day = Days.ofCode(Integer.parseInt(part)).getDays();
            serviceDays.append(day).append(", ");
        }
        return serviceDays.substring(0, serviceDays.length() - 2);
    }
    public static List<String> serviceDaysToStringList(String dbData) {
        List<String> daysList = new ArrayList<>();
        String[] parts = dbData.split(", ");
        for (String part : parts) {
            String day = Days.ofString(part).getDays();
            daysList.add(day);
        }
        return daysList;
    }
}
