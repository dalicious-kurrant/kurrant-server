package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.QOrderCart;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static co.dalicious.domain.order.entity.QOrderCart.orderCart;

@Repository
@RequiredArgsConstructor
public class QOrderCartRepository {

    public final JPAQueryFactory queryFactory;


    public BigInteger getCartId(BigInteger id) {
        return queryFactory
                .select(orderCart.id)
                .from(orderCart)
                .where(orderCart.id.eq(id))
                .fetchOne();
    }
}
