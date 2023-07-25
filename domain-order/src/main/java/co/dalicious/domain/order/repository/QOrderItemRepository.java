package co.dalicious.domain.order.repository;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.order.entity.QOrder.order;
import static co.dalicious.domain.order.entity.QOrderItem.orderItem;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;

@Repository
@RequiredArgsConstructor
public class QOrderItemRepository {

    private final JPAQueryFactory queryFactory;


    public void updateStatusToSeven(BigInteger orderItemId) {
        queryFactory.update(orderItem)
                .set(orderItem.orderStatus, OrderStatus.CANCELED)
                .where(orderItem.id.eq(orderItemId))
                .execute();
    }

    public List<OrderItem> findByUserAndOrderStatusBeforeToday(User user, OrderStatus orderStatus, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(orderItem)
                .leftJoin(orderItemDailyFood).on(orderItem.id.eq(orderItemDailyFood.id))
                .where(orderItem.orderStatus.eq(orderStatus),
                        orderItem.order.user.eq(user),
                        orderItemDailyFood.dailyFood.serviceDate.between(startDate, endDate))
                .fetch();
    }

    public OrderItem findByUserAndOrderId(User user, BigInteger orderItemId) {
        return queryFactory.selectFrom(orderItem)
                .where(orderItem.id.eq(orderItemId),
                        orderItem.order.user.eq(user),
                        orderItem.orderStatus.eq(OrderStatus.RECEIPT_COMPLETE))
                .fetchOne();
    }
}
