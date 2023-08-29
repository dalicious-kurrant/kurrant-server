package co.kurrant.batch.service;

import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final EntityManager entityManager;

    public List<BigInteger> findUserIdsByReviewDeadline() {

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate limitDay = today.minusDays(5);
        LocalDateTime before24ByNow = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(1);

        String queryString = "SELECT u.id, df.serviceDate, oidf.deliveryTime " +
                "FROM OrderItem oi " +
                "JOIN OrderItemDailyFood oidf ON oi.id = oidf.id " +
                "JOIN oidf.dailyFood df " +
                "JOIN Order o ON oi.order = o " +
                "JOIN User u ON o.user = u " +
                "WHERE oi.orderStatus = 11L AND df.serviceDate between :limitDay and :today " +
                "AND u.firebaseToken IS NOT NULL " +
                "GROUP BY u.id, df.serviceDate, oidf.deliveryTime";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        query.setParameter("today", today);
        query.setParameter("limitDay", limitDay);
        List<Object[]> results = query.getResultList();

        List<BigInteger> userIds = new ArrayList<>();
        for (Object[] result : results) {
            BigInteger userId = (BigInteger) result[0];
            LocalDate serviceDate = (LocalDate) result[1];
            LocalTime pickupTime = (LocalTime) result[2];

            String deadlineTime = DateUtils.calculatedDDayAndTime(serviceDate.atTime(pickupTime));
            String day = deadlineTime.split(" ")[0];

            if (day.equals("0")) {
                userIds.add(userId);
            }
        }

        String batchQueryString = "SELECT bpal.userId, bpal.pushDateTime " +
                "FROM BatchPushAlarmLog bpal " +
                "WHERE bpal.pushCondition = 2001";

        TypedQuery<Object[]> batchQuery = entityManager.createQuery(batchQueryString, Object[].class);
        List<Object[]> batchResults = batchQuery.getResultList();

        for(Object[] result : batchResults) {
            BigInteger userId = (BigInteger) result[0];
            LocalDateTime pushDateTime = (LocalDateTime) result[1];

            if(pushDateTime.isAfter(before24ByNow)) userIds.remove(userId);
        }

        return userIds;
    }

}
