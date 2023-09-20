package co.kurrant.batch.service;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodCapacity;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.repository.FoodCapacityRepository;
import co.dalicious.domain.food.repository.MakersCapacityRepository;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                "AND (df.dailyFoodStatus = 1 OR df.dailyFoodStatus = 2 OR df.dailyFoodStatus = 4)";

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
        log.info("[고객사 주문 마감 시간 지난 DailyFood 읽기 시작] : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));

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

    public Set<BigInteger> overLastOverTimeDailyFood() {

        log.info("[메이커스 주문 마감 판매중/품절 DailyFood 읽기 시작] : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));

        Set<BigInteger> dailyFoodIds = new HashSet<>();

        String queryString = "SELECT df.id, ffc.lastOrderTime, df.serviceDate " +
                "FROM FoodCapacity ffc " +
                "         LEFT JOIN DailyFood df ON df.food = ffc.food " +
                "WHERE df.diningType = ffc.diningType " +
                "  AND ffc.lastOrderTime IS NOT NULL " +
                "  AND df.food = ffc.food " +
                "  AND (df.dailyFoodStatus = 1 or df.dailyFoodStatus = 2)";
        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        for (Object[] result : results) {
            BigInteger dailyFoodId = (BigInteger) result[0];
            DayAndTime lastOrderDayAndTime = (DayAndTime) result[1];
            LocalDate serviceDate = (LocalDate) result[2];
            LocalDate lastOrderDate = serviceDate.minusDays(lastOrderDayAndTime.getDay());

            DayOfWeek dayOfWeek = lastOrderDate.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY) {
                lastOrderDate = lastOrderDate.minusDays(1);
            } else if (dayOfWeek == DayOfWeek.SUNDAY) {
                lastOrderDate = lastOrderDate.minusDays(2);

            }
            LocalDateTime lastOrderDateTime = lastOrderDate.atTime(lastOrderDayAndTime.getTime());

            if (LocalDateTime.now().isAfter(lastOrderDateTime) || LocalDateTime.now().isEqual(lastOrderDateTime)) {
                dailyFoodIds.add(dailyFoodId);
            }
        }

        String queryString2 = "SELECT df.id, mmc.lastOrderTime, df.serviceDate " +
                "FROM MakersCapacity mmc " +
                "         LEFT JOIN Makers mm on mmc.makers = mm " +
                "         LEFT JOIN Food f on f.makers = mm " +
                "         LEFT JOIN DailyFood df on df.food = f " +
                "where df.diningType = mmc.diningType " +
                "  and mmc.lastOrderTime is not null " +
                "  and df.food = f " +
                "  and (df.dailyFoodStatus = 1 or df.dailyFoodStatus = 2)";
        TypedQuery<Object[]> query2 = entityManager.createQuery(queryString2, Object[].class);
        List<Object[]> results2 = query2.getResultList();

        for (Object[] result : results2) {
            BigInteger dailyFoodId = (BigInteger) result[0];
            DayAndTime lastOrderDayAndTime = (DayAndTime) result[1];
            LocalDate serviceDate = (LocalDate) result[2];
            LocalDate lastOrderDate = serviceDate.minusDays(lastOrderDayAndTime.getDay());
            LocalDateTime lastOrderDateTime = lastOrderDate.atTime(lastOrderDayAndTime.getTime());

            if (LocalDateTime.now().isAfter(lastOrderDateTime) || LocalDateTime.now().isEqual(lastOrderDateTime)) {
                dailyFoodIds.add(dailyFoodId);
            }
        }
        return dailyFoodIds;
    }
}
