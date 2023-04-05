package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.PaymentCancelHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static co.dalicious.domain.order.entity.QPaymentCancelHistory.paymentCancelHistory;

@Repository
@RequiredArgsConstructor
public class QPaymentCancelHistoryRepository {

    private final JPAQueryFactory queryFactory;

    public List<PaymentCancelHistory> findAllByIds(Set<BigInteger> ids) {
        return queryFactory.selectFrom(paymentCancelHistory)
                .where(paymentCancelHistory.id.in(ids))
                .fetch();
    }
}
