package co.kurrant.batch.service;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushAlarmService {

    private final EntityManager entityManager;

    public List<BigInteger> getGroupsForOneHourLeftLastOrderTime() {
        List<BigInteger> groupIds = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();

        log.info("[고객사 주문 마감 시간 Group 읽기 시작] : {}", DateUtils.localDateTimeToString(currentTime));

        // 고객사 주문 마감 시간 그룹 조회
        String queryStringForGroup = "SELECT g.id, df.serviceDate, mi.lastOrderTime, fc.lastOrderTime, mc.lastOrderTime " +
                "FROM DailyFood df " +
                "LEFT JOIN df.group g " +
                "LEFT JOIN MealInfo mi ON mi.group.id = g.id AND mi.diningType = df.diningType  " +
                "LEFT JOIN df.food f " +
                "LEFT JOIN FoodCapacity fc ON f = df.food AND fc.diningType = df.diningType " +
                "LEFT JOIN Makers m ON m = f.makers " +
                "LEFT JOIN MakersCapacity mc ON mc.makers = m " +
                "WHERE fc.lastOrderTime IS NOT NULL " +
                "  and mc.lastOrderTime IS NOT NULL " +
                "  and (df.dailyFoodStatus = 1 or df.dailyFoodStatus = 2)";

        TypedQuery<Object[]> queryForGroup = entityManager.createQuery(queryStringForGroup, Object[].class);
        List<Object[]> resultsForGroup = queryForGroup.getResultList();

        for (Object[] result : resultsForGroup) {
            BigInteger groupId = (BigInteger) result[0];
            LocalDate serviceDate = (LocalDate) result[1];

            List<LocalDateTime> lastOrderDateTime = new ArrayList<>();

            DayAndTime lastOrderDayAndTimeByGroup = (DayAndTime) result[2];
            LocalDate lastOrderDateByGroup = serviceDate.minusDays(lastOrderDayAndTimeByGroup.getDay());
            lastOrderDateTime.add(lastOrderDateByGroup.atTime(lastOrderDayAndTimeByGroup.getTime()).minusHours(1));

            DayAndTime lastOrderDayAndTimeByFood = (DayAndTime) result[3];
            LocalDate lastOrderDateByFood = serviceDate.minusDays(lastOrderDayAndTimeByFood.getDay());
            lastOrderDateTime.add(lastOrderDateByFood.atTime(lastOrderDayAndTimeByFood.getTime()).minusHours(1));

            DayAndTime lastOrderDayAndTimeByMakers = (DayAndTime) result[4];
            LocalDate lastOrderDateByMakers = serviceDate.minusDays(lastOrderDayAndTimeByMakers.getDay());
            lastOrderDateTime.add(lastOrderDateByMakers.atTime(lastOrderDayAndTimeByMakers.getTime()).minusHours(1));

            Collections.sort(lastOrderDateTime);

            if (currentTime.equals(lastOrderDateTime.get(0))) {
                groupIds.add(groupId);
            }
        }

        return groupIds;
    }

//    public List<BigInteger> getGroupIdsByStartDate() {
//        List<BigInteger> groupIds = new ArrayList<>();
//        LocalDateTime currentTime = LocalDateTime.now();
//        LocalDate currentDate = currentTime.toLocalDate();
//
//        log.info("[상태 변경 Group 읽기 시작] : {}", DateUtils.localDateTimeToString(currentTime));
//
//        // 고객사 주문 마감 시간 그룹 조회
//        String queryStringForGroup = "SELECT msz.id, msz.openDate " +
//                "FROM MySpotZone msz " +
//                "WHERE msz.openDate > :currentDate " +
//                "AND msz.mySpotZoneStatus != 1";
//
//        TypedQuery<Object[]> queryForGroup = entityManager.createQuery(queryStringForGroup, Object[].class);
//        queryForGroup.setParameter("currentDate", currentDate);
//        List<Object[]> resultsForGroup = queryForGroup.getResultList();
//
//        for (Object[] result : resultsForGroup) {
//            MySpotZone mySpotZone = (MySpotZone) result[0];
//            LocalDate startDate = (LocalDate) result[1];
//
//
//        }
//    }

}
