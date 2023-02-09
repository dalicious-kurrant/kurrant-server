package co.kurrant.app.public_api.service;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TimeStampTest {
    @Test
    public void Timestamp_test() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        System.out.println(now);
    }
}
