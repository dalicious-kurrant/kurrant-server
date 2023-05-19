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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushAlarmService {

    private final EntityManager entityManager;

    public Set<BigInteger> getGroupsForOneHourLeftLastOrderTime() {
        Set<BigInteger> groupIds = new HashSet<>();
        LocalDateTime currentTime = LocalDateTime.now();

        log.info("[고객사 주문 마감 시간 Group 읽기 시작] : {}", DateUtils.localDateTimeToString(currentTime));

        // 고객사 주문 마감 시간 그룹 조회
        String queryStringForGroup = "SELECT df.id, mi.lastOrderTime, df.serviceDate " +
                "FROM DailyFood df " +
                "JOIN df.group g " +
                "JOIN MealInfo mi ON mi.group.id = g.id AND mi.diningType = df.diningType  " +
                "  and (df.dailyFoodStatus = 1 or df.dailyFoodStatus = 2)";

        TypedQuery<Object[]> queryForGroup = entityManager.createQuery(queryStringForGroup, Object[].class);
        List<Object[]> resultsForGroup = queryForGroup.getResultList();

        for (Object[] result : resultsForGroup) {
            BigInteger groupId = (BigInteger) result[0];
            DayAndTime lastOrderDayAndTime = (DayAndTime) result[1];
            LocalDate serviceDate = (LocalDate) result[2];
            LocalDate lastOrderDate = serviceDate.minusDays(lastOrderDayAndTime.getDay());
            LocalDateTime lastOrderDateTime = lastOrderDate.atTime(lastOrderDayAndTime.getTime()).minusHours(1);

            if (currentTime.equals(lastOrderDateTime)) {
                groupIds.add(groupId);
            }
        }

        log.info("[음식 주문 마감 시간 Group 읽기 시작] : {}", DateUtils.localDateTimeToString(currentTime));

        // 음식 주문 마감 시간 그룹 조회
        String queryStringForFood = "SELECT g.id, ffc.lastOrderTime, df.serviceDate " +
                "FROM FoodCapacity ffc " +
                "         LEFT JOIN DailyFood df ON df.food = ffc.food AND df.diningType = ffc.diningType " +
                "         LEFT JOIN Group g ON df.group.id = g.id " +
                "WHERE ffc.lastOrderTime IS NOT NULL " +
                "  and (df.dailyFoodStatus = 1 or df.dailyFoodStatus = 2)";

        TypedQuery<Object[]> queryForFood = entityManager.createQuery(queryStringForFood, Object[].class);
        List<Object[]> resultsForFood = queryForFood.getResultList();

        for (Object[] result : resultsForFood) {
            BigInteger groupId = (BigInteger) result[0];
            DayAndTime lastOrderDayAndTime = (DayAndTime) result[1];
            LocalDate serviceDate = (LocalDate) result[2];
            LocalDate lastOrderDate = serviceDate.minusDays(lastOrderDayAndTime.getDay());
            LocalDateTime lastOrderDateTime = lastOrderDate.atTime(lastOrderDayAndTime.getTime()).minusHours(1);

            if (currentTime.equals(lastOrderDateTime)) {
                groupIds.add(groupId);
            }
        }

        log.info("[메이커스 주문 마감 시간 Group 읽기 시작] : {}", DateUtils.localDateTimeToString(currentTime));

        String queryStringForMakers = "SELECT g.id, mmc.lastOrderTime, df.serviceDate " +
                "FROM MakersCapacity mmc " +
                "         LEFT JOIN Makers mm on mmc.makers = mm " +
                "         LEFT JOIN Food f on f.makers = mm " +
                "         LEFT JOIN DailyFood df on df.food = f AND df.diningType = mmc.diningType " +
                "         LEFT JOIN Group g on g = df.group " +
                "where mmc.lastOrderTime is not null " +
                "  and (df.dailyFoodStatus = 1 or df.dailyFoodStatus = 2)";
        TypedQuery<Object[]> queryForMakers = entityManager.createQuery(queryStringForMakers, Object[].class);
        List<Object[]> resultsForMakers = queryForMakers.getResultList();

        for (Object[] result : resultsForMakers) {
            BigInteger groupId = (BigInteger) result[0];
            DayAndTime lastOrderDayAndTime = (DayAndTime) result[1];
            LocalDate serviceDate = (LocalDate) result[2];
            LocalDate lastOrderDate = serviceDate.minusDays(lastOrderDayAndTime.getDay());
            LocalDateTime lastOrderDateTime = lastOrderDate.atTime(lastOrderDayAndTime.getTime());

            if (currentTime.equals(lastOrderDateTime)) {
                groupIds.add(groupId);
            }
        }
        return groupIds;
    }
}
