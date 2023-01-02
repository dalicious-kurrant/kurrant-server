package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.order.entity.QOrderItem.orderItem;

@Repository
@RequiredArgsConstructor
public class QOrderItemRepository {

    public final JPAQueryFactory queryFactory;


    public List<OrderItem> findByServiceDateBetween(LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(orderItem)
                .where(orderItem.serviceDate.between(startDate,endDate))
                .fetch();
    }
}
