package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItemDailyFood;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;


@Repository
@RequiredArgsConstructor
public class QOrderDailyFoodRepository {

    public final JPAQueryFactory queryFactory;

    public List<OrderItemDailyFood> findByServiceDateBetween(LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.serviceDate.between(startDate,endDate))
                .fetch();
    }

    public  List<OrderItemDailyFood> findByServiceDate(LocalDate today) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.serviceDate.eq(today))
                .fetch();
    }
}
