package co.dalicious.system.util;

import co.dalicious.system.enums.Days;

import java.util.*;
import java.util.stream.Collectors;

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

    public static List<Days> serviceDaysToDaysList(String serviceDays) {
        String[]  serviceDayArr = serviceDays.split(", |,");
        List<Days> serviceDaysList = new ArrayList<>();
        if(serviceDays.isEmpty()) return serviceDaysList;
        for(String serviceDay : serviceDayArr) {
            serviceDaysList.add(Days.ofString(serviceDay));
        }
        return serviceDaysList;
    }

    public static String serviceDaysToDaysString(Collection<Days> daysCollection) {
        StringBuilder stringBuilder = new StringBuilder();
        if(daysCollection == null || daysCollection.isEmpty()) return null;
        for(Days days : daysCollection) {
            stringBuilder.append(days.getDays()).append(", ");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 2);
    }

    public static List<String> serviceDaysToDaysStringList(List<Days> daysList) {
        List<String> daysStringList = new ArrayList<>();
        if(daysList == null || daysList.isEmpty()) return daysStringList;
        daysStringList = daysList.stream().map(Days::getDays).collect(Collectors.toList());
        return daysStringList;
    }

    public static String serviceDaysSetToString(Set<Days> daysList) {
        StringBuilder stringBuilder = new StringBuilder();
        if(daysList == null || daysList.isEmpty()) return String.valueOf(stringBuilder);
        for(Days days : daysList) {
            stringBuilder.append(days.getDays()).append(", ");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 2);
    }

    public static String stringToDaysStringList(List<Days> daysList) {
        StringBuilder stringBuilder = new StringBuilder();
        if(daysList == null || daysList.isEmpty()) return String.valueOf(stringBuilder);
        for(Days days : daysList) {
            stringBuilder.append(days.getDays()).append(", ");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 2);
    }
}
