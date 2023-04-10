package co.dalicious.domain.order.repository;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.Cart;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.order.entity.QCart.cart;
import static co.dalicious.domain.order.entity.QCartDailyFood.cartDailyFood;

@Repository
@RequiredArgsConstructor
public class QCartItemRepository {

    public final JPAQueryFactory queryFactory;

    public void deleteByCartId(List<Cart> carts) {
        queryFactory.delete(cart)
                .where(cart.in(carts))
                .execute();
    }

    public void deleteByUserAndCartDailyFoodId(User user, BigInteger id) {
        queryFactory
                .delete(cart)
                .where(cart.user.eq(user) , cart.id.eq(id))
                .execute();
    }

   public List<CartDailyFood> findByUserAndDailyFoods(User user, List<DailyFood> dailyFoodIds) {
        return queryFactory.selectFrom(cartDailyFood)
                .where(cartDailyFood.dailyFood.in(dailyFoodIds), cartDailyFood.user.eq(user))
                .fetch();
   }
}
