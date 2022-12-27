package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderCartItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static co.dalicious.domain.order.entity.QOrderCartItem.orderCartItem;

@Repository
@RequiredArgsConstructor
public class QOrderCartItemRepository {

    public final JPAQueryFactory queryFactory;

    public void deleteByCartId(Integer cartId) {
        queryFactory
                .delete(orderCartItem)
                .where(orderCartItem.orderCart.id.eq(cartId))
                .execute();
    }

    public void deleteByFoodId(Integer id, Integer foodId) {
        queryFactory
                .delete(orderCartItem)
                .where(orderCartItem.orderCart.id.eq(id),
                        orderCartItem.foodId.id.eq(foodId))
                .execute();
    }

    public void updateByFoodId(OrderCartItem updateCartItem) {
        queryFactory
                .update(orderCartItem)
                .set(orderCartItem.count, updateCartItem.getCount())
                .where(orderCartItem.foodId.id.eq(updateCartItem.getFoodId().getId()),
                        orderCartItem.orderCart.id.eq(updateCartItem.getOrderCart().getId()))
                .execute();
    }
}
