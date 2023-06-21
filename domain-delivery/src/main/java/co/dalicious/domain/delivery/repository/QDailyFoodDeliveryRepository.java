package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.client.entity.CorporationSpot;
import co.dalicious.domain.client.entity.MySpot;
import co.dalicious.domain.client.entity.OpenGroupSpot;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static co.dalicious.domain.delivery.entity.QDailyFoodDelivery.dailyFoodDelivery;
import static co.dalicious.domain.delivery.entity.QDeliveryInstance.deliveryInstance;
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

    public List<DailyFoodDelivery> findByFilter(LocalDate startDate, LocalDate endDate, GroupDataType groupDataType, Makers makers, DiningType diningType, LocalTime deliveryTime, String deliveryCode, User user) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if (startDate != null) {
            whereClause.and(dailyFoodDelivery.deliveryInstance.serviceDate.goe(startDate));
        }
        if (endDate != null) {
            whereClause.and(dailyFoodDelivery.deliveryInstance.serviceDate.loe(endDate));
        }
        if (groupDataType != null) {
            Class<? extends Spot> spotClass = null;
            switch (groupDataType) {
                case CORPORATION -> spotClass = CorporationSpot.class;
                case MY_SPOT -> spotClass = MySpot.class;
                case OPEN_GROUP -> spotClass = OpenGroupSpot.class;
            }
            whereClause.and(dailyFoodDelivery.deliveryInstance.spot.instanceOf(spotClass));
        }
        if (makers != null) {
            whereClause.and(dailyFoodDelivery.deliveryInstance.makers.eq(makers));
        }
        if (diningType != null) {
            whereClause.and(dailyFoodDelivery.deliveryInstance.diningType.eq(diningType));
        }
        if (deliveryTime != null) {
            whereClause.and(dailyFoodDelivery.deliveryInstance.deliveryTime.eq(deliveryTime));
        }
        if (deliveryCode != null) {
            String serviceDateString = deliveryCode.substring(0, 8);
            LocalDate serviceDate = LocalDate.parse(serviceDateString, DateTimeFormatter.ofPattern("yyyyMMdd"));
            String makersId = deliveryCode.substring(8, 11);
            String orderNumber = deliveryCode.split("-")[1];

            whereClause.and(dailyFoodDelivery.deliveryInstance.serviceDate.eq(serviceDate));
            whereClause.and(dailyFoodDelivery.deliveryInstance.makers.id.eq(BigInteger.valueOf(Long.parseLong(makersId))));
            whereClause.and(dailyFoodDelivery.deliveryInstance.orderNumber.eq(Integer.valueOf(orderNumber)));
        }
        if (user != null) {
            whereClause.and(dailyFoodDelivery.orderItemDailyFood.order.user.eq(user));
        }

        return queryFactory.selectFrom(dailyFoodDelivery)
                .where(whereClause)
                .fetch();
    }
}
