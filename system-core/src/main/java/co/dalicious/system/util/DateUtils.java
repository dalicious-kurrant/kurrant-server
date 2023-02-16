package co.dalicious.system.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
    public static String toISO(Date date) {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return sdf.format(date);
    }

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
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
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static String timeToStringWithAMPM(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public static LocalDate stringToDate(String strLocalDate) {
        String[] stringList = strLocalDate.split("-");
        return LocalDate.of(Integer.parseInt(stringList[0]),Integer.parseInt(stringList[1]), Integer.parseInt(stringList[2]));
    }

    public static String localDateToString(LocalDate date) { return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); }

    public static long calculatedDDay(String limitDay, String today) throws ParseException {
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date limit = new Date(parseFormat.parse(limitDay).getTime());
        Date present = new Date(parseFormat.parse(today).getTime());
        return (limit.getTime() - present.getTime()) / (24 * 60 * 60 * 1000);
    }
}
