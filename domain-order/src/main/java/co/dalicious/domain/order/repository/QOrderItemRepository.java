package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.enums.OrderStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collections;

import static co.dalicious.domain.order.entity.QOrder.order;
import static co.dalicious.domain.order.entity.QOrderItem.orderItem;

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


}
