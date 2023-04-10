package co.kurrant.batch.service;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyFoodService {
    private final EntityManager entityManager;
    public List<BigInteger> matchingDailyFoodIds() {
        log.info("[판매중/품절 DailyFood 읽기 시작] : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));

        String queryString = "SELECT df.id, mi.lastOrderTime, df.serviceDate " +
                "FROM DailyFood df " +
                "JOIN df.group g " +
                "JOIN MealInfo mi ON mi.group.id = g.id " +
                "WHERE mi.diningType = df.diningType " +
                "  AND (df.dailyFoodStatus = 1 OR df.dailyFoodStatus = 2)";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        List<BigInteger> dailyFoodIds = new ArrayList<>();
        for (Object[] result : results) {
            BigInteger dailyFoodId = (BigInteger) result[0];
            DayAndTime lastOrderDayAndTime = (DayAndTime) result[1];
            LocalDate serviceDate = (LocalDate) result[2]; // Fetch the serviceDate from the DailyFood entity
            LocalDate lastOrderDate = serviceDate.minusDays(lastOrderDayAndTime.getDay());
            LocalDateTime lastOrderDateTime = lastOrderDate.atTime(lastOrderDayAndTime.getTime());

            if (LocalDateTime.now().isAfter(lastOrderDateTime) || LocalDateTime.now().isEqual(lastOrderDateTime)) {
                dailyFoodIds.add(dailyFoodId);
            }
        }
        return dailyFoodIds;
    }

    public List<BigInteger> overLastOrderTimeDailyFoodIds() {
        log.info("[판매중/품절 DailyFood 읽기 시작] : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));

        String queryString = "SELECT df.id, mi.lastOrderTime, df.serviceDate " +
                "FROM DailyFood df " +
                "JOIN df.group g " +
                "JOIN MealInfo mi ON mi.group.id = g.id " +
                "WHERE mi.diningType = df.diningType " +
                "  AND df.dailyFoodStatus = 6";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        List<BigInteger> dailyFoodIds = new ArrayList<>();
        for (Object[] result : results) {
            BigInteger dailyFoodId = (BigInteger) result[0];
            DayAndTime lastOrderDayAndTime = (DayAndTime) result[1];
            LocalDate serviceDate = (LocalDate) result[2]; // Fetch the serviceDate from the DailyFood entity
            LocalDate lastOrderDate = serviceDate.minusDays(lastOrderDayAndTime.getDay());
            LocalDateTime lastOrderDateTime = lastOrderDate.atTime(lastOrderDayAndTime.getTime());

            if (LocalDateTime.now().isAfter(lastOrderDateTime) || LocalDateTime.now().isEqual(lastOrderDateTime)) {
                dailyFoodIds.add(dailyFoodId);
            }
        }

        return dailyFoodIds;
    }
}
