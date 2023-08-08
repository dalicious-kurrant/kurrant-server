package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.QOrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;

@Repository
@RequiredArgsConstructor
public class QOrderItemDailyFoodRepository {

    private final JPAQueryFactory queryFactory;

    public OrderItemDailyFood findAllByUserAndDailyFood(BigInteger userId, BigInteger dailyFoodId) {

        /* userId로 주문내역을 조회(order)
        *  order중에 serviceDay가 5일 이내이면서
        *  foodId가 일치하는것 조회*/

        System.out.println(dailyFoodId + "dailyFoodId");
        return queryFactory.selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.id.eq(userId),
                        orderItemDailyFood.dailyFood.id.eq(dailyFoodId),
                        orderItemDailyFood.dailyFood.serviceDate.between(LocalDate.now().minusDays(5), LocalDate.now()))
                .limit(1)
                .fetchOne();
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
