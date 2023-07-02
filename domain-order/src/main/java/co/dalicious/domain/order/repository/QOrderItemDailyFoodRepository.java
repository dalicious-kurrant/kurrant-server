package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;
import static co.dalicious.domain.order.entity.QOrder.order;

@Repository
@RequiredArgsConstructor
public class QOrderItemDailyFoodRepository {

    private final JPAQueryFactory queryFactory;

    public OrderItemDailyFood findAllByUserAndDailyFood(BigInteger userId, BigInteger foodId) {

        /*
        * userId로 주문내역을 조회(order)
        * order중에 serviceDay가 5일 이내이면서
        * foodId가 일치하는것 조회
        * */
        List<BigInteger> orderIds = queryFactory.select(order.id)
                                                .from(order)
                                                .where(order.user.id.eq(userId))
                                                .fetch();
        Set<BigInteger> orderItemDailyFoodIdSet = new HashSet<>();
        for (BigInteger orderId : orderIds){
            OrderItemDailyFood orderItemDailyFoods = queryFactory.selectFrom(orderItemDailyFood)
                    .where(orderItemDailyFood.order.id.eq(orderId),
                            orderItemDailyFood.dailyFood.serviceDate.between(LocalDate.now().minusDays(5), LocalDate.now()))
                    .limit(1)
                    .fetchOne();
            if (orderItemDailyFoods != null){
                orderItemDailyFoodIdSet.add(orderItemDailyFoods.getId());
            }
        }
        Iterator<BigInteger> orderItemDailyFoodIterator = orderItemDailyFoodIdSet.iterator();
        List<BigInteger> resultIds = new ArrayList<>();
        while(orderItemDailyFoodIterator.hasNext()){
            resultIds.add(orderItemDailyFoodIterator.next());
        }

        for (BigInteger orderItemDailyFoodId : resultIds) {
            OrderItemDailyFood orderItemDailyFoods = queryFactory.selectFrom(orderItemDailyFood)
                    .where(orderItemDailyFood.id.eq(orderItemDailyFoodId))
                    .fetchOne();
            if (orderItemDailyFoods.getDailyFood().getFood().getId().equals(foodId)) return orderItemDailyFoods;
        }

        return null;
    }

    public List<OrderItemDailyFood> findAllByUserAndPeriod(User user, LocalDate startDate, LocalDate endDate) {
        return queryFactory.selectFrom(orderItemDailyFood)
                .leftJoin(orderItemDailyFood.dailyFood, dailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()),
                        dailyFood.serviceDate.goe(startDate),
                        dailyFood.serviceDate.loe(endDate))
                .fetch();
    }

}
