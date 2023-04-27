package co.kurrant.batch.service;

import co.dalicious.domain.order.repository.QOrderItemRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final EntityManager entityManager;

    public List<BigInteger> findOrderItemByReviewDeadline() {
        LocalDate limitDay = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(7);
        // order status -> RECEIPT_COMPLETE 이고 남은 리뷰 작성 기한이 0일이면서 현재시간이 deliveryTime 보다 전일때.
        String queryString = "SELECT oi.id, df.serviceDate, mi.deliveryTime\n" +
                "FROM OrderItem oi\n" +
                "JOIN OrderItemDailyFood oidf ON oi.id = oidf.id\n" +
                "JOIN oidf.dailyFood df\n" +
                "JOIN df.group g\n" +
                "JOIN MealInfo mi ON g.id = mi.group.id AND df.diningType = mi.diningType\n" +
                "WHERE oi.orderStatus = 11L";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        List<BigInteger> orderIds = new ArrayList<>();
        for(Object[] result : results) {
            BigInteger orderItemId = (BigInteger) result[0];
            LocalDate serviceDate = (LocalDate) result[1];
            LocalTime deliveryTime = (LocalTime) result[2];

            if(serviceDate == null) continue;
            if(deliveryTime == null) deliveryTime = LocalTime.MAX;

            String reviewableDate = DateUtils.calculatedDDayAndTime(LocalDateTime.of(serviceDate, deliveryTime));
            String day = reviewableDate.split(" ")[0];
            LocalTime time = DateUtils.stringToLocalTime(reviewableDate.split(" ")[1]);

            if(day.equals("1") && Objects.requireNonNull(time).isBefore(LocalTime.MAX)) {
                orderIds.add(orderItemId);
            }
        }


        return orderIds;
    }

}
