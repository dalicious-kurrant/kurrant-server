package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static co.dalicious.domain.delivery.entity.QDailyFoodDelivery.dailyFoodDelivery;
import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.food.entity.QFood.food;
import static co.dalicious.domain.food.entity.QMakers.makers;
import static co.dalicious.domain.order.entity.QOrderDailyFood.orderDailyFood;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;

@Repository
@RequiredArgsConstructor
public class QDailyFoodDeliveryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<DailyFoodDelivery> findByFilter(User user, Makers selectedMakers, Spot spot, LocalDate serviceDate, DiningType diningType, LocalTime deliveryTime) {
        return Optional.ofNullable(queryFactory.selectFrom(dailyFoodDelivery)
                .leftJoin(dailyFoodDelivery.orderItemDailyFood, orderItemDailyFood)
                .leftJoin(orderDailyFood).on(orderItemDailyFood.order.id.eq(orderDailyFood.id))
                .leftJoin(orderItemDailyFood.dailyFood, dailyFood)
                .leftJoin(dailyFood.food, food)
                .leftJoin(food.makers, makers)
                .where(orderItemDailyFood.order.user.eq(user),
                        makers.eq(selectedMakers),
                        orderDailyFood.spot.eq(spot),
                        orderItemDailyFood.dailyFood.serviceDate.eq(serviceDate),
                        orderItemDailyFood.dailyFood.diningType.eq(diningType),
                        orderItemDailyFood.deliveryTime.eq(deliveryTime))
                .fetchOne());
    }
}
