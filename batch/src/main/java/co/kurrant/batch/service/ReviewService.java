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

    public List<BigInteger> findOrderItemByReviewDeadline() {

        // order status -> RECEIPT_COMPLETE 이고 남은 리뷰 작성 기한이 0일이면서 현재시간이 deliveryTime 보다 전일때.
        String queryString = "SELECT oi.id, df.serviceDate, mi.deliveryTime\n" +
                "FROM OrderItem oi\n" +
                "JOIN OrderItemDailyFood oidf ON oi.id = oidf.id\n" +
                "JOIN oidf.dailyFood df\n" +
                "JOIN df.group g\n" +
                "JOIN MealInfo mi ON g.id = mi.group.id AND df.diningType = mi.diningType\n" +
                "WHERE oi.orderStatus = 11L AND df.serviceDate > :limitDay";

        LocalDate limitDay = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(4);
        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        query.setParameter("limitDay", limitDay);
        List<Object[]> results = query.getResultList();

        List<BigInteger> orderIds = new ArrayList<>();
        for(Object[] result : results) {
            BigInteger orderItemId = (BigInteger) result[0];
            LocalDate serviceDate = (LocalDate) result[1];
            LocalTime deliveryTime = (LocalTime) result[2];

            if(serviceDate == null) continue;
            if(deliveryTime == null) deliveryTime = LocalTime.MAX;
            LocalDateTime reviewableDateTime = serviceDate.plusDays(4).atTime(deliveryTime);
            String reviewableDate = DateUtils.calculatedDDayAndTime(reviewableDateTime);
            String day = reviewableDate.split(" ")[0];
            LocalTime time = DateUtils.stringToLocalTime(reviewableDate.split(" ")[1]);

            if(day.equals("1") && Objects.requireNonNull(time).isBefore(LocalTime.MAX)) {
                orderIds.add(orderItemId);
            }
        }

        return orderIds;
    }

    public List<BigInteger> findUserIds() {

        // order status -> RECEIPT_COMPLETE 이고 남은 리뷰 작성 기한이 0일이면서 현재시간이 deliveryTime 보다 전일때.
        List<BigInteger> orderItemIds = findOrderItemByReviewDeadline();
        if (orderItemIds.isEmpty()) {
            // Return an empty reader if orderItemIds is empty
            return null;
        }

        String queryStringByOrderItemIds = "SELECT u.id FROM OrderItem oi JOIN Order o ON oi.order = o JOIN User u ON o.user = u WHERE oi.id in :orderItemIds";

        TypedQuery<BigInteger> queryByOrderItemIds = entityManager.createQuery(queryStringByOrderItemIds, BigInteger.class);
        queryByOrderItemIds.setParameter("orderItemIds", orderItemIds);
        List<BigInteger> resultsByOrderItemIds = queryByOrderItemIds.getResultList();

        List<BigInteger> userIds = new ArrayList<>(resultsByOrderItemIds);
        if(userIds.isEmpty()) {
            return null;
        }

        String queryStringByUser = "SELECT u.id, bpal.pushDateTime FROM BatchPushAlarmLog bpal JOIN User u ON bpal.userId = u.id WHERE u.id in :userIds AND bpal.pushCondition = 2001";

        TypedQuery<Object[]> queryByUser = entityManager.createQuery(queryStringByUser, Object[].class);
        queryByUser.setParameter("userIds", userIds);
        List<Object[]> resultsByUser = queryByUser.getResultList();

        //pushDateTime 이 24 전이면 push 안보냄
        LocalDateTime before24ByNow = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(1);
        List<BigInteger> removeUserIds = new ArrayList<>();
        for(Object[] result : resultsByUser) {
            BigInteger userId = (BigInteger) result[0];
            LocalDateTime pushDateTime = (LocalDateTime) result[1];

            if(pushDateTime.isAfter(before24ByNow)) {
                removeUserIds.add(userId);
            }
        }

        userIds.removeAll(removeUserIds);

        return userIds;
    }

}
