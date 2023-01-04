package co.kurrant.app.public_api.service;

import co.dalicious.system.util.DateUtils;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class TimeStampTest {
    @Test
    public void Timestamp_test() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        System.out.println(now);
    }

    @Test
    public void test() {
        LocalDate firstPaidDate = LocalDate.of(2022 , 11, 21);
        LocalDate now = LocalDate.now();

        Period period = firstPaidDate.until(now);
        System.out.println(period.getMonths());
    }

    @Test
    public void timestampTest() {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        System.out.println(timestamp);

        String formattedString = DateUtils.format(timestamp, "yyyy. MM. dd");
        System.out.println(formattedString);
    }
}
