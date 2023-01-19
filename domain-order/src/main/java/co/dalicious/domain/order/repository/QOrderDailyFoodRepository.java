package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderDailyFood;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.order.entity.QOrderDailyFood.orderDailyFood;

@Repository
@RequiredArgsConstructor
public class QOrderDailyFoodRepository {

    public final JPAQueryFactory queryFactory;

    public List<OrderDailyFood> findByServiceDateBetween(LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(orderDailyFood)
                .where(orderDailyFood.serviceDate.between(startDate,endDate))
                .fetch();
    }
}
