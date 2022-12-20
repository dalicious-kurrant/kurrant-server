package co.kurrant.app.public_api.service;

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
}
