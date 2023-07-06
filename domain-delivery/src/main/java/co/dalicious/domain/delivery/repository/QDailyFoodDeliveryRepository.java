package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static co.dalicious.domain.client.entity.QSpot.spot;
import static co.dalicious.domain.delivery.entity.QDailyFoodDelivery.dailyFoodDelivery;
import static co.dalicious.domain.delivery.entity.QDeliveryInstance.deliveryInstance;
import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.food.entity.QFood.food;
import static co.dalicious.domain.food.entity.QMakers.makers;
import static co.dalicious.domain.order.entity.QOrder.order;
import static co.dalicious.domain.order.entity.QOrderDailyFood.orderDailyFood;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;
import static co.dalicious.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QDailyFoodDeliveryRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

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
                .limit(1)
                .fetchOne());
    }

    public List<DailyFoodDelivery> findByFilter(LocalDate startDate, LocalDate endDate, GroupDataType groupDataType, Makers makers, DiningType diningType, LocalTime deliveryTime, String deliveryCode, User selectedUser) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if (startDate != null) {
            whereClause.and(deliveryInstance.serviceDate.goe(startDate));
        }
        if (endDate != null) {
            whereClause.and(deliveryInstance.serviceDate.loe(endDate));
        }
        if (groupDataType != null) {
            switch (groupDataType) {
                // FIXME: 체인로지스 페이지에는 기업이 추가되지 않음
                //case CORPORATION -> whereClause.and(spot.instanceOf(CorporationSpot.class));
                case MY_SPOT -> whereClause.and(spot.instanceOf(MySpot.class));
                case OPEN_GROUP -> whereClause.and(spot.instanceOf(OpenGroupSpot.class));
            }
        }
        if (makers != null) {
            whereClause.and(deliveryInstance.makers.eq(makers));
        }
        if (diningType != null) {
            whereClause.and(deliveryInstance.diningType.eq(diningType));
        }
        if (deliveryTime != null) {
            whereClause.and(deliveryInstance.deliveryTime.eq(deliveryTime));
        }
        if (deliveryCode != null) {
            String serviceDateString = deliveryCode.substring(0, 8);
            LocalDate serviceDate = LocalDate.parse(serviceDateString, DateTimeFormatter.ofPattern("yyyyMMdd"));
            int dashPosition = deliveryCode.indexOf("-");
            String makersId = deliveryCode.substring(8, dashPosition);
            String orderNumber = deliveryCode.substring(dashPosition + 1);

            whereClause.and(deliveryInstance.serviceDate.eq(serviceDate));
            whereClause.and(deliveryInstance.makers.id.eq(BigInteger.valueOf(Long.parseLong(makersId))));
            whereClause.and(deliveryInstance.orderNumber.eq(Integer.valueOf(orderNumber)));
        }
        if (selectedUser != null) {
            whereClause.and(user.eq(selectedUser));
        }

        return queryFactory.selectFrom(dailyFoodDelivery)
                .leftJoin(dailyFoodDelivery.deliveryInstance, deliveryInstance)
                .leftJoin(deliveryInstance.spot, spot)
                .leftJoin(dailyFoodDelivery.orderItemDailyFood, orderItemDailyFood)
                .leftJoin(orderItemDailyFood.order, order)
                .leftJoin(order.user, user)
                .where(whereClause, orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()), spot.instanceOf(MySpot.class).or(spot.instanceOf(OpenGroupSpot.class)))
                .fetch();
    }
}
