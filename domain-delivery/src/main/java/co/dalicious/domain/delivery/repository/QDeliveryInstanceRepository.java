package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.OpenGroup;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static co.dalicious.domain.client.entity.QGroup.group;
import static co.dalicious.domain.delivery.entity.QDailyFoodDelivery.dailyFoodDelivery;
import static co.dalicious.domain.delivery.entity.QDeliveryInstance.deliveryInstance;
import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;

@Repository
@RequiredArgsConstructor
public class QDeliveryInstanceRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<DeliveryInstance> findBy(LocalDate serviceDate, DiningType diningType, LocalTime deliveryTime, Makers makers, Spot spot) {
        return Optional.ofNullable(queryFactory.selectFrom(deliveryInstance)
                .where(deliveryInstance.deliveryTime.eq(deliveryTime),
                        deliveryInstance.diningType.eq(diningType),
                        deliveryInstance.serviceDate.eq(serviceDate),
                        deliveryInstance.makers.eq(makers),
                        deliveryInstance.spot.eq(spot))
                .fetchOne());
    }

    public List<DeliveryInstance> findAllBy(LocalDate serviceDate, DiningType diningType, LocalTime deliveryTime, Makers makers, Group group) {
        return queryFactory.selectFrom(deliveryInstance)
                .where(deliveryInstance.deliveryTime.eq(deliveryTime),
                        deliveryInstance.diningType.eq(diningType),
                        deliveryInstance.serviceDate.eq(serviceDate),
                        deliveryInstance.makers.eq(makers),
                        deliveryInstance.spot.group.eq(group))
                .fetch();
    }

    public List<DeliveryInstance> findAllBy(LocalDate serviceDate, DiningType diningType, LocalTime deliveryTime, List<String> makersNames, String groupName) {
        return queryFactory.selectFrom(deliveryInstance)
                .where(deliveryInstance.deliveryTime.eq(deliveryTime),
                        deliveryInstance.diningType.eq(diningType),
                        deliveryInstance.serviceDate.eq(serviceDate),
                        deliveryInstance.makers.name.in(makersNames),
                        deliveryInstance.spot.group.name.eq(groupName))
                .fetch();
    }

    public List<DeliveryInstance> findByFilter(LocalDate startDate, LocalDate endDate, List<DiningType> diningTypes, Makers makers) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if (startDate != null) {
            whereClause.and(deliveryInstance.serviceDate.goe(startDate));
        }
        if (endDate != null) {
            whereClause.and(deliveryInstance.serviceDate.loe(endDate));
        }
        if (diningTypes != null && !diningTypes.isEmpty()) {
            whereClause.and(deliveryInstance.diningType.in(diningTypes));
        }
        return queryFactory.selectFrom(deliveryInstance)
                .where(deliveryInstance.makers.eq(makers),
                        whereClause)
                .fetch();
    }

    public List<DeliveryInstance> findByPeriod(LocalDate startDate, LocalDate endDate) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if (startDate != null) {
            whereClause.and(deliveryInstance.serviceDate.goe(startDate));
        }
        if (endDate != null) {
            whereClause.and(deliveryInstance.serviceDate.loe(endDate));
        }
        return queryFactory.selectFrom(deliveryInstance)
                .where(whereClause)
                .fetch();
    }


    public Integer getMaxOrderNumber(LocalDate serviceDate, DiningType diningType, LocalTime deliveryTime, Makers makers) {
        Integer maxOrderNumber = queryFactory.select(deliveryInstance.orderNumber.max())
                .from(deliveryInstance)
                .where(deliveryInstance.serviceDate.eq(serviceDate),
                        deliveryInstance.diningType.eq(diningType),
                        deliveryInstance.deliveryTime.eq(deliveryTime),
                        deliveryInstance.makers.eq(makers))
                .fetchOne();

        return Objects.requireNonNullElse(maxOrderNumber, 0);
    }

    public List<DeliveryInstance> findByDailyFoodAndOrderStatus(LocalDate start, LocalDate end, List<Group> groups, List<Spot> spotList) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if (start != null) {
            whereClause.and(deliveryInstance.serviceDate.goe(start));
        }
        if (end != null) {
            whereClause.and(deliveryInstance.serviceDate.loe(end));
        }
        if (groups != null && !groups.isEmpty()) {
            whereClause.and(group.in(groups));
        }
        if (spotList != null && !spotList.isEmpty()) {
            whereClause.and(deliveryInstance.spot.in(spotList));
        }

        return queryFactory.selectFrom(deliveryInstance)
                .leftJoin(dailyFoodDelivery).on(deliveryInstance.dailyFoodDeliveries.contains(dailyFoodDelivery))
                .leftJoin(dailyFoodDelivery.orderItemDailyFood, orderItemDailyFood)
                .leftJoin(orderItemDailyFood.dailyFood, dailyFood)
                .leftJoin(dailyFood.group, group)
                .where(whereClause, orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()))
                .distinct()
                .fetch();
    }

    public List<LocalTime> getTodayDeliveryTimes() {
        return queryFactory.select(deliveryInstance.deliveryTime)
                .from(deliveryInstance)
                .where(deliveryInstance.serviceDate.eq(LocalDate.now()))
                .distinct()
                .fetch();
    }
}
