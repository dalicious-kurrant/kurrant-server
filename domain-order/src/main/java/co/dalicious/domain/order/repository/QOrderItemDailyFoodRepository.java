package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItemDailyFood;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
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
        List<BigInteger> orderIds = queryFactory.select(order.id)
                                                .from(order)
                                                .where(order.user.id.eq(userId))
                                                .fetch();

        List<BigInteger> dailyFoodIds = queryFactory.select(dailyFood.id)
                .from(dailyFood)
                .where(dailyFood.food.id.eq(foodId))
                .fetch();

        Set<BigInteger> orderItemDailyFoodIdSet = new HashSet<>();
        for (BigInteger orderId : orderIds){
            BigInteger orderItemDailyFoodId = queryFactory.select(orderItemDailyFood.id)
                    .from(orderItemDailyFood)
                    .where(orderItemDailyFood.id.eq(orderId))
                    .fetchOne();
            orderItemDailyFoodIdSet.add(orderItemDailyFoodId);
        }

        for (BigInteger dailyFoodId : dailyFoodIds){
            BigInteger orderItemDailyFoodId = queryFactory.select(orderItemDailyFood.id)
                    .from(orderItemDailyFood)
                    .where(orderItemDailyFood.dailyFood.id.eq(dailyFoodId))
                    .fetchOne();
            orderItemDailyFoodIdSet.add(orderItemDailyFoodId);
        }
        orderItemDailyFoodIdSet.remove(null);
        Iterator<BigInteger> resultTemp = orderItemDailyFoodIdSet.iterator();
        List<BigInteger> sortList = new ArrayList<>();
        while (resultTemp.hasNext()){
            sortList.add(resultTemp.next());
        }
        BigInteger id = sortList.stream().sorted().toList().get(sortList.size()-1);


        return queryFactory.selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.id.eq(id))
                .fetchOne();
    }
}