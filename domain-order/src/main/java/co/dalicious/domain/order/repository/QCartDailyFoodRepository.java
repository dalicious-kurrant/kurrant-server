package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.client.entity.QGroup.group;
import static co.dalicious.domain.client.entity.QSpot.spot;
import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
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

     public List<CartDailyFood> findAllByUserFetchGroupAndSpot(User user) {
        return queryFactory.selectFrom(cartDailyFood)
                .innerJoin(cartDailyFood.spot, spot).fetchJoin()
                .innerJoin(spot.group, group).fetchJoin()
                .where(cartDailyFood.user.eq(user))
                .fetch();
     }

    public void deleteByCartDailyFoodList(List<CartDailyFood> cartDailyFoodList) {
        queryFactory.delete(cartDailyFood)
                .where(cartDailyFood.in(cartDailyFoodList))
                .execute();
    }

    public List<CartDailyFood> findAllByUserOrderBySpotAndServiceDateAndDiningType(User user) {
        return queryFactory.select(cartDailyFood)
                .leftJoin(cartDailyFood.dailyFood, dailyFood).fetchJoin()
                .leftJoin(cartDailyFood.spot, spot).fetchJoin()
                .where(cartDailyFood.user.eq(user))
                .orderBy(spot.id.asc(), dailyFood.serviceDate.asc(), dailyFood.diningType.asc())
                .fetch();
    }
}
