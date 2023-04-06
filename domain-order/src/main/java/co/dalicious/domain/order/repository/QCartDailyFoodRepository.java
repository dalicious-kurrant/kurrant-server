package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.CartDailyFood;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.order.entity.QCartDailyFood.cartDailyFood;


@Repository
@RequiredArgsConstructor
public class QCartDailyFoodRepository {
    public final JPAQueryFactory queryFactory;
    public List<CartDailyFood> findAllByFoodIds(List<BigInteger> cartDailyFoodIds) {
        return queryFactory
                .selectFrom(cartDailyFood)
                .where(cartDailyFood.id.in(cartDailyFoodIds))
                .fetch();
    }

    public void deleteByCartDailyFoodList(List<CartDailyFood> cartDailyFoodList) {
        queryFactory.delete(cartDailyFood)
                .where(cartDailyFood.in(cartDailyFoodList))
                .execute();
    }
}
