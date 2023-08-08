package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.QOrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;
import static co.dalicious.domain.order.entity.QOrder.order;

@Repository
@RequiredArgsConstructor
public class QOrderItemDailyFoodRepository {

    private final JPAQueryFactory queryFactory;

    public OrderItemDailyFood findAllByUserAndDailyFood(BigInteger userId, BigInteger dailyFoodId) {

        /*
        * userId로 주문내역을 조회(order)
        * order중에 serviceDay가 5일 이내이면서
        * foodId가 일치하는것 조회
        * */
        OrderItemDailyFood orderItemDailyFood = queryFactory.selectFrom(QOrderItemDailyFood.orderItemDailyFood)
                .where(QOrderItemDailyFood.orderItemDailyFood.order.user.id.eq(userId),
                        QOrderItemDailyFood.orderItemDailyFood.dailyFood.id.eq(dailyFoodId),
                        QOrderItemDailyFood.orderItemDailyFood.dailyFood.serviceDate.between(LocalDate.now().minusDays(5), LocalDate.now()))
                .limit(1)
                .fetchOne();

        return orderItemDailyFood;

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
