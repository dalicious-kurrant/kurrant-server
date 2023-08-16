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
import java.time.ZoneId;
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

    public List<BigInteger> getMySpotZoneOpenPushAlarmUserId() {

        LocalDateTime before24ByNow = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(1);

        String queryString = "select u.id " +
                                     "from MySpotZone msz " +
                                     "left join UserGroup ug on ug.group.id = msz.id and ug.clientStatus = 1 " +
                                     "left join User u on ug.user = u " +
                                     "where msz.mySpotZoneStatus = 1 " +
                                     "group by u.id";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        List<BigInteger> userIds = new ArrayList<>();
        for (Object[] result : results) {
            BigInteger userId = (BigInteger) result[0];
            userIds.add(userId);
        }

        String logQueryString = "select u.id, bpal.pushDateTime " +
                "from BatchPushAlarmLog bpal " +
                "left join User u on bpal.userId = u.id " +
                "where bpal.pushCondition = 4001";

        TypedQuery<Object[]> logQuery = entityManager.createQuery(logQueryString, Object[].class);
        List<Object[]> logResults = logQuery.getResultList();

        for (Object[] result : logResults) {
            BigInteger userId = (BigInteger) result[0];
            LocalDateTime logDateTime = (LocalDateTime) result[1];

            if(logDateTime.isAfter(before24ByNow)) userIds.remove(userId);
        }

        return userIds;
    }

}
