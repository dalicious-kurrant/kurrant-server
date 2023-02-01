package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.QOrder;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.DateUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static co.dalicious.domain.order.entity.QOrder.order;
import static co.dalicious.domain.order.entity.QOrderItem.orderItem;

@Repository
@RequiredArgsConstructor
public class QOrderRepository {

    private final JPAQueryFactory queryFactory;


    public void afterPaymentUpdate(String receiptUrl, String paymentKey, BigInteger orderId) {
        long update = queryFactory.update(order)
                .set(order.receiptUrl, receiptUrl)
                .set(order.paymentKey, paymentKey)
                .where(order.id.eq(orderId))
                .execute();

        if (update != 1){
            throw new ApiException(ExceptionEnum.UPDATE_ORDER_FAILED);
        }
    }

    public String getPaymentKey(BigInteger orderItemId) {
        return queryFactory.select(order.paymentKey)
                .from(order,orderItem)
                .where(order.id.eq(orderItem.order.id),
                        orderItem.id.eq(orderItemId))
                .fetchOne();
    }

    public List<Order> findAllOrderByUserFilterByOrderTypeAndPeriod(User user, Integer orderType, LocalDate startDate, LocalDate endDate) {
        BooleanExpression whereClause = order.user.eq(user);
        if (orderType != null) {
            whereClause = whereClause.and(order.orderType.eq(OrderType.ofCode(orderType)));
        } else {
            whereClause = whereClause.and(order.orderType.in(OrderType.values()));
        }
        if (startDate != null) {
            whereClause = whereClause.and(order.createdDateTime.goe(DateUtils.localDateToTimestamp(startDate)));
        }
        if (endDate != null) {
            whereClause = whereClause.and(order.createdDateTime.lt(DateUtils.localDateToTimestamp(endDate.plusDays(1))));
        }
        return queryFactory.selectFrom(order)
                .where(whereClause)
                .orderBy(order.createdDateTime.desc())
                .fetch();
    }
}
