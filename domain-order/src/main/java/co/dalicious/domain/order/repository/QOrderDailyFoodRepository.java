package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.user.entity.User;
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

    public List<OrderItemDailyFood> findByUserAndServiceDateBetween(User user, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.orderStatus.eq(OrderStatus.COMPLETED),
                        orderItemDailyFood.orderItemDailyFoodGroup.serviceDate.between(startDate,endDate))
                .fetch();
    }

    public  List<OrderItemDailyFood> findByServiceDate(LocalDate today) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.serviceDate.eq(today))
                .fetch();
    }

    public List<OrderItemDailyFood> findAllMealScheduleByUser(User user) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.dailyFood.serviceDate.goe(LocalDate.now()))
                .fetch();
    }
}
