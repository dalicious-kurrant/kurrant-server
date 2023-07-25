package co.dalicious.system.util;

import org.springframework.cglib.core.Local;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DateUtils {
    private static final String SEPARATOR = ",";
    public static String toISO(Timestamp ts) {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return sdf.format(ts);
    }

    public static String toISOLocalDate(Timestamp ts) {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(ts);
    }

    public static String format(Date date, String formatString) {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(formatString);
        return sdf.format(date);
    }

    public static String format(LocalDate date, String formatString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString);
        return date.format(formatter);
    }

    public static String format(Timestamp timestamp, String formatString) {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(formatString);
        return sdf.format(timestamp);
    }

    public static String format(LocalDate date) {
        if(date == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static String formatWithoutSeparator(LocalDate date) {
        if(date == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(formatter);
    }

    public static String format(Timestamp timestamp) {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }

    public static Timestamp localDateToTimestamp(LocalDate localDate) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 0, 0, 0);
        return Timestamp.valueOf(localDateTime);
    }

    public static LocalTime stringToTime(String str, String separator) {
        String[] strings = str.split(separator);
        Integer[] integers = new Integer[2];
        for(int i = 0 ; i < strings.length; i++) {
            integers[i] = Integer.parseInt(strings[i]);
        }
        return LocalTime.of(integers[0], integers[1]);
    }

    public static String timeToString(LocalTime time) {
        return (time == null) ? null : time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static String timesToString(List<LocalTime> times) {
        if (times == null || times.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (LocalTime deliveryTime : times) {
            sb.append(DateUtils.timeToString(deliveryTime)).append(SEPARATOR);
        }

        // remove the last separator
        sb.setLength(sb.length() - SEPARATOR.length());

        return sb.toString();
    }

    public static List<String> timesToStringList(List<LocalTime> times) {
        List<String> stringList = new ArrayList<>();
        for (LocalTime time : times) {
            stringList.add(timeToString(time));
        }
        return stringList;
    }

    public static String timeToStringWithAMPM(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public static LocalDate stringToDate(String strLocalDate) {
        if(strLocalDate == null) return null;
        String[] stringList = strLocalDate.split("-");
        return LocalDate.of(Integer.parseInt(stringList[0]),Integer.parseInt(stringList[1]), Integer.parseInt(stringList[2]));
    }

    public static String localDateToString(LocalDate date) { return date == null ? null : date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); }

    public static String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static LocalDateTime stringToLocalDateTime(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(string, formatter);
    }
    public static LocalTime stringToLocalTime(String time) {
        if (time == null) {
            return null;
        }

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("HH:mm")
                .optionalStart()
                .appendPattern(":ss")
                .optionalEnd()
                .toFormatter();

        return LocalTime.parse(time.trim(), formatter);
    }

    public static List<LocalTime> stringToLocalTimes(String timesStr) {
        String[] timeStrArr = timesStr.split(",|, ");
        List<LocalTime> times = new ArrayList<>();
        for (String timeStr : timeStrArr) {
            times.add(DateUtils.stringToLocalTime(timeStr));
        }
        return times;
    }


    public static Map<String, LocalDate> getWeekOfDay(LocalDate today) {
        Map<String, LocalDate> weekOfDay = new HashMap<>();
        String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA);
        LocalDate startDate = switch (dayOfWeek) {
            case "화" -> today.minusDays(1);
            case "수" -> today.minusDays(2);
            case "목" -> today.minusDays(3);
            case "금" -> today.minusDays(4);
            case "토" -> today.minusDays(5);
            case "일" -> today.minusDays(6);
            default -> today;
        };
        LocalDate endDate = startDate.plusDays(7);

        weekOfDay.put("startDate", startDate);
        weekOfDay.put("endDate", endDate);

        return weekOfDay;
    }

    public static YearMonth toYearMonth(Integer year, Integer month) {
        return YearMonth.of(year, month);
    }

    public static String YearMonthToString(YearMonth yearMonth) {
        return yearMonth.getYear() + "-" + yearMonth.getMonthValue();
    }

    public static String calculatedDDayAndTime(LocalDateTime limitDayAndTime) {
        LocalDateTime now = LocalDateTime.now();

        if(limitDayAndTime.isBefore(now)){
            limitDayAndTime = limitDayAndTime.plusDays(5);
        }

        long dateTime = ChronoUnit.MINUTES.between(now, limitDayAndTime);
        long day = dateTime % 60 % 24;
        long hour = (dateTime - (dateTime % 60 % 24)) % 60;
        long min = (dateTime - (dateTime % 60 % 24 % 60));

        long leftDay = ChronoUnit.DAYS.between(now.toLocalDate(), limitDayAndTime.toLocalDate());
        if(now.toLocalTime().isAfter(limitDayAndTime.toLocalTime())) leftDay = leftDay - 1;
        long hoursLeft = now.until(limitDayAndTime, ChronoUnit.HOURS);
        hoursLeft = hoursLeft % 24;
        now = now.plusHours(hoursLeft);
        long minutesLeft = now.until(limitDayAndTime, ChronoUnit.MINUTES);
        minutesLeft = minutesLeft % 60;

        LocalTime remainingTime = LocalTime.of((int) hoursLeft, (int) minutesLeft);

        return String.format("%01d %tH:%tM", leftDay, remainingTime, remainingTime);
    }

    public static YearMonth stringToYearMonth(String startYearMonth) {
        return YearMonth.parse(startYearMonth.substring(0, 4) + "-" + startYearMonth.substring(4));
    }
    public static String toISOLocalDateAndWeekOfDay(Timestamp ts) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일 E", Locale.KOREA);
        return sdf.format(ts);
    }

    public static boolean isBetween(LocalTime timeToCheck, LocalTime minTime, LocalTime maxTime) {
        return !timeToCheck.isBefore(minTime) && !timeToCheck.isAfter(maxTime);
    }
}
