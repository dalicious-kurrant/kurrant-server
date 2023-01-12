package co.dalicious.system.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
}
