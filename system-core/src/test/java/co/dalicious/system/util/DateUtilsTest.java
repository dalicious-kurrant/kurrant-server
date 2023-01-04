package co.dalicious.system.util;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void stringToTime() {
        String strTime = "08:00:00";
        LocalTime time = DateUtils.stringToTime(strTime, ":");
        System.out.println("time = " + time);
        System.out.println("hour = " + time.getHour());
        System.out.println("minute = " + time.getMinute());
        System.out.println("second = " + time.getSecond());

        String strTime2 = "08:30:30";
        LocalTime time2 = DateUtils.stringToTime(strTime2, ":");
        System.out.println("time2 = " + time);
        System.out.println("hour2 = " + time2.getHour());
        System.out.println("minute2 = " + time2.getMinute());
        System.out.println("second2 = " + time2.getSecond());
    }
}