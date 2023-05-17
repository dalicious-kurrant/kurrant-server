package co.kurrant.batch.service;

import co.dalicious.domain.order.repository.QOrderItemRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
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

        LocalDate limitDay = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(6);
        LocalDateTime before24ByNow = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(1);

        String queryString = "SELECT u.id, bpal.pushDateTime " +
                "FROM OrderItem oi " +
                "JOIN OrderItemDailyFood oidf ON oi.id = oidf.id " +
                "JOIN oidf.dailyFood df " +
                "JOIN df.group g " +
                "JOIN MealInfo mi ON g.id = mi.group.id AND df.diningType = mi.diningType " +
                "JOIN Order o ON oi.order = o " +
                "JOIN User u ON o.user = u " +
                "LEFT JOIN BatchPushAlarmLog bpal ON bpal.userId = u.id AND bpal.pushCondition = 2001 " +
                "WHERE oi.orderStatus = 11L AND df.serviceDate > :limitDay " +
                "AND u.firebaseToken IS NOT NULL " +
                "GROUP BY u.id, bpal.pushDateTime";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        query.setParameter("limitDay", limitDay);
        List<Object[]> results = query.getResultList();

        List<BigInteger> userIds = new ArrayList<>();
        for (Object[] result : results) {
            BigInteger userId = (BigInteger) result[0];
            LocalDateTime pushDateTime = (LocalDateTime) result[1];

            if (pushDateTime == null || pushDateTime.isBefore(before24ByNow)) {
                userIds.add(userId);
            }
        }

        return userIds;
    }

}
