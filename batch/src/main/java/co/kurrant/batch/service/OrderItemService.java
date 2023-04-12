package co.kurrant.batch.service;

import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final EntityManager entityManager;

    public List<BigInteger> matchingOrderStatusByWaitDelivery() {
        log.info("[OrderItem id 찾기] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        // list up order item daily food by order status = delivering
        String queryString = "SELECT df.serviceDate, dfg.pickupTime, oi.id " +
                "FROM OrderItem oi " +
                "JOIN OrderItemDailyFood oidf ON oi.id = oidf.id " +
                "JOIN oidf.dailyFood df " +
                "JOIN df.dailyFoodGroup dfg " +
                "WHERE oi.orderStatus = 6L";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        LocalDate today  = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));

        List<BigInteger> orderItemIds = new ArrayList<>();
        for(Object[] objects : results) {
            LocalDate serviceDate = (LocalDate) objects[0];
            LocalTime pickupTime = (LocalTime) objects[1];
            BigInteger orderItemId = (BigInteger) objects[2];
            if(serviceDate.equals(today) && pickupTime.isBefore(now)) {
                orderItemIds.add(orderItemId);
            }
        }

        return orderItemIds;
    }

    public List<BigInteger> matchingOrderStatusByWaitDelivering() {
        log.info("[OrderItem id 찾기] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        // list up order item daily food by order status = delivering
        String queryString = "SELECT oi.id, df.serviceDate, mi.deliveryTime " +
                "FROM OrderItem oi " +
                "JOIN OrderItemDailyFood oidf ON oi.id = oidf.id " +
                "JOIN oidf.dailyFood df " +
                "JOIN df.group g " +
                "JOIN MealInfo mi ON g.id = mi.group.id AND df.diningType = mi.diningType " +
                "WHERE oi.orderStatus = 9L";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        LocalDate today  = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));

        List<BigInteger> orderItemIds = new ArrayList<>();
        for(Object[] objects : results) {
            BigInteger orderItemId = (BigInteger) objects[0];
            LocalDate serviceDate = (LocalDate) objects[1];
            LocalTime deliveryTime = (LocalTime) objects[2];
            if(serviceDate.equals(today) && deliveryTime.isBefore(now)) {
                orderItemIds.add(orderItemId);
            }
        }

        return orderItemIds;
    }
}
