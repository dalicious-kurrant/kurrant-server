package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderCart;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

import static co.dalicious.domain.order.entity.QOrderCart.orderCart;

@Repository
@RequiredArgsConstructor
public class QOrderCartRepository {

    public final JPAQueryFactory queryFactory;


    public BigInteger getCartId(BigInteger id) {
        return queryFactory
                .select(orderCart.id)
                .from(orderCart)
                .where(orderCart.userId.eq((id)))
                .fetchOne();
    }


    public Optional<OrderCart> findOneByUserId(BigInteger id) {
        return Optional.ofNullable(queryFactory
                .selectFrom(orderCart)
                .where(orderCart.userId.eq(id))
                .fetchOne());
    }

    public Boolean existsByUserId(BigInteger id) {
        return queryFactory.from(orderCart)
                .where(orderCart.userId.eq(id))
                .select(orderCart.userId)
                .fetchFirst() != null;
    }

    public void CreateOrderCart(BigInteger id) {
         queryFactory.insert(orderCart)
                 .columns(orderCart.userId)
                 .values(id)
                 .execute();
    }
}
