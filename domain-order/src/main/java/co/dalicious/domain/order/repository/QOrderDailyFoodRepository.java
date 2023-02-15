package co.dalicious.domain.order.repository;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.payment.entity.enums.PaymentCompany;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.food.entity.QFood.food;
import static co.dalicious.domain.food.entity.QMakers.makers;
import static co.dalicious.domain.order.entity.QOrderDailyFood.orderDailyFood;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;


@Repository
@RequiredArgsConstructor
public class QOrderDailyFoodRepository {

    public final JPAQueryFactory queryFactory;

    public void afterPaymentUpdate(String receiptUrl, String paymentKey, BigInteger orderId, PaymentCompany paymentCompany) {
        long update = queryFactory.update(orderDailyFood)
                .set(orderDailyFood.receiptUrl, receiptUrl)
                .set(orderDailyFood.paymentKey, paymentKey)
                .set(orderDailyFood.paymentCompany, paymentCompany)
                .where(orderDailyFood.id.eq(orderId))
                .execute();
    }

    public List<OrderItemDailyFood> findByUserAndServiceDateBetween(User user, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.orderStatus.in(OrderStatus.COMPLETED, OrderStatus.WAIT_DELIVERY, OrderStatus.DELIVERING, OrderStatus.DELIVERED, OrderStatus.RECEIPT_COMPLETE),
                        orderItemDailyFood.orderItemDailyFoodGroup.serviceDate.between(startDate,endDate))
                .fetch();
    }

    public  List<OrderItemDailyFood> findByServiceDate(LocalDate today) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.dailyFood.serviceDate.eq(today))
                .fetch();
    }

    public List<OrderItemDailyFood> findAllWhichGetMembershipBenefit(User user, LocalDateTime now, LocalDateTime threeMonthAgo) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.createdDateTime.between(Timestamp.valueOf(threeMonthAgo),Timestamp.valueOf(now)),
                        orderItemDailyFood.orderStatus.eq(OrderStatus.COMPLETED),
                        orderItemDailyFood.membershipDiscountRate.gt(0))
                .fetch();
    }

    public List<OrderItemDailyFood> findAllMealScheduleByUser(User user) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.dailyFood.serviceDate.goe(LocalDate.now()))
                .fetch();
    }

    public OrderItemDailyFood findByUserAndDailyFood(User user, DailyFood dailyFood) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.dailyFood.eq(dailyFood))
                .fetchOne();
    }

    public List<OrderItemDailyFood> findAllByUserAndOrderStatus(User user, OrderStatus orderStatus) {
        return  queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.orderStatus.eq(orderStatus))
                .fetch();
    }

    public Integer getFoodCount(DailyFood selectedDailyFood) {
        int count = 0;
        List<OrderItemDailyFood> orderItemDailyFoods = queryFactory.selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.dailyFood.food.eq(selectedDailyFood.getFood()),
                        orderItemDailyFood.dailyFood.serviceDate.eq(selectedDailyFood.getServiceDate()),
                        orderItemDailyFood.dailyFood.diningType.eq(selectedDailyFood.getDiningType()),
                        orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()))
                .fetch();
        for (OrderItemDailyFood itemDailyFood : orderItemDailyFoods) {
            count += itemDailyFood.getCount();
        }
        return count;
    }
    public Integer getMakersCount(DailyFood selectedDailyFood) {
        int count = 0;
        List<OrderItemDailyFood> orderItemDailyFoods = queryFactory.selectFrom(orderItemDailyFood)
                .innerJoin(orderItemDailyFood.dailyFood, dailyFood)
                .innerJoin(dailyFood.food, food)
                .innerJoin(food.makers, makers)
                .where(makers.eq(selectedDailyFood.getFood().getMakers()),
                        dailyFood.serviceDate.eq(selectedDailyFood.getServiceDate()),
                        dailyFood.diningType.eq(selectedDailyFood.getDiningType()),
                        orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()))
                .fetch();
        for (OrderItemDailyFood itemDailyFood : orderItemDailyFoods) {
            count += itemDailyFood.getCount();
        }
        return count;
    }
}
