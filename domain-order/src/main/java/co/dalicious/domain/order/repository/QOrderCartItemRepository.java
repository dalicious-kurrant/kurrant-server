package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderCartItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.order.entity.QOrderCartItem.orderCartItem;

@Repository
@RequiredArgsConstructor
public class QOrderCartItemRepository {

    public final JPAQueryFactory queryFactory;

    public void deleteByCartId(BigInteger cartId) {
        queryFactory
                .delete(orderCartItem)
                .where(orderCartItem.orderCart.id.eq(cartId))
                .execute();
    }

    public void deleteByFoodId(BigInteger id, Integer dailyFoodId) {
        queryFactory
                .delete(orderCartItem)
                .where(orderCartItem.orderCart.id.eq(id),
                        orderCartItem.dailyFood.id.eq(BigInteger.valueOf(dailyFoodId)))
                .execute();
    }

    public void updateByFoodId(OrderCartItem updateCartItem) {
        queryFactory
                .update(orderCartItem)
                .set(orderCartItem.count, updateCartItem.getCount())
                .where(orderCartItem.dailyFood.id.eq(updateCartItem.getDailyFood().getId()),
                        orderCartItem.orderCart.id.eq(updateCartItem.getOrderCart().getId()))
                .execute();
    }

    public List<OrderCartItem> getItems(BigInteger cartId) {
        return queryFactory
                .selectFrom(orderCartItem)
                .where(orderCartItem.orderCart.id.eq(cartId))
                .fetch();
    }

    public List<OrderCartItem> findDuplicatedItem(BigInteger cartId, BigInteger dailyFoodId) {
        return queryFactory
                .selectFrom(orderCartItem)
                .where(orderCartItem.orderCart.id.eq(cartId),
                        orderCartItem.dailyFood.id.eq(dailyFoodId))
                .fetch();
    }


    public List<OrderCartItem> getUserCartItemList(BigInteger id) {
        return queryFactory.selectFrom(orderCartItem)
                .where(orderCartItem.orderCart.userId.eq(id))
                .fetch();
    }

    public void updateCount(BigInteger id) {
            queryFactory.update(orderCartItem)
                .where(orderCartItem.id.eq(id))
                .set(orderCartItem.count, orderCartItem.count.add(1))
                .execute();
    }
}
