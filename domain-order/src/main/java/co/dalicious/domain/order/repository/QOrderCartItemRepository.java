package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.domain.order.entity.OrderItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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

    public void deleteByFoodId(Integer id, Integer dailyFoodId) {
        queryFactory
                .delete(orderCartItem)
                .where(orderCartItem.orderCart.id.eq(id),
                        orderCartItem.dailyFood.id.eq(dailyFoodId))
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

    public List<OrderCartItem> getItems(Integer cartId) {
        return queryFactory
                .selectFrom(orderCartItem)
                .where(orderCartItem.orderCart.id.eq(cartId))
                .fetch();
    }
}
