package co.dalicious.domain.order.repository;

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

    public void updateByFoodId(BigInteger cartItemId, Integer count) {
        queryFactory
                .update(cart)
                .set(cart.count, count)
                .where(cart.id.eq(cartItemId))
                .execute();
    }

    public List<Cart> getItems(BigInteger cartId) {
        return queryFactory
                .selectFrom(cart)
                .where(cart.id.eq(cartId))
                .fetch();
    }

    public List<CartDailyFood> findDuplicatedItem(BigInteger cartId, BigInteger dailyFoodId) {
        return queryFactory
                .selectFrom(cartDailyFood)
                .where(cart.id.eq(cartId),
                        cartDailyFood.dailyFood.id.eq(dailyFoodId))
                .fetch();
    }


    public List<CartDailyFood> getUserCartItemList(BigInteger id) {
        return queryFactory.selectFrom(cartDailyFood)
                .where(cartDailyFood.id.eq(id))
                .fetch();
    }

    public void updateCount(BigInteger id) {
            queryFactory.update(cartDailyFood)
                .where(cartDailyFood.id.eq(id))
                .set(cartDailyFood.count, cartDailyFood.count.add(1))
                .execute();
    }
}
