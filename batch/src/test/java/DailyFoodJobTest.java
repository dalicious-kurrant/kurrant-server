import co.dalicious.domain.client.entity.DayAndTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DailyFoodJobTest {
    @Test
    void test_1() {
        DayAndTime lastOrderDayAndTime = new DayAndTime(0, LocalTime.of(10, 0));
        LocalDate serviceDate = LocalDate.of(2023, 3, 30); // Fetch the serviceDate from the DailyFood entity
        LocalDate lastOrderDate = serviceDate.minusDays(lastOrderDayAndTime.getDay());
        LocalDateTime lastOrderDateTime = lastOrderDate.atTime(lastOrderDayAndTime.getTime());

        Assertions.assertTrue(LocalDateTime.now().isAfter(lastOrderDateTime) || LocalDateTime.now().isEqual(lastOrderDateTime));
    }
}
