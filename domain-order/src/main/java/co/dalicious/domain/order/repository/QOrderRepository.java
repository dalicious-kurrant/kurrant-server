package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.QOrder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static co.dalicious.domain.order.entity.QOrder.order;

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
                .from(order)
                .where(order.orderItems.any().id.eq(orderItemId))
                .fetchOne();
    }
}
