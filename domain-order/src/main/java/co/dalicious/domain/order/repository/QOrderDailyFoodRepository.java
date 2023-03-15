package co.dalicious.domain.order.repository;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.dto.CapacityDto;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.payment.entity.enums.PaymentCompany;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static co.dalicious.domain.client.entity.QGroup.group;
import static co.dalicious.domain.client.entity.QSpot.spot;
import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.food.entity.QFood.food;
import static co.dalicious.domain.food.entity.QMakers.makers;
import static co.dalicious.domain.order.entity.QOrder.order;
import static co.dalicious.domain.order.entity.QOrderDailyFood.orderDailyFood;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;
import static com.querydsl.core.group.GroupBy.sum;


@Repository
@RequiredArgsConstructor
public class QOrderDailyFoodRepository {

    public final JPAQueryFactory queryFactory;
    public List<OrderItemDailyFood> findByUserAndServiceDateBetween(User user, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.orderStatus.in(OrderStatus.COMPLETED, OrderStatus.WAIT_DELIVERY, OrderStatus.DELIVERING, OrderStatus.DELIVERED, OrderStatus.RECEIPT_COMPLETE),
                        orderItemDailyFood.orderItemDailyFoodGroup.serviceDate.between(startDate, endDate))
                .fetch();
    }

    public List<OrderItemDailyFood> findByServiceDate(LocalDate today) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.dailyFood.serviceDate.eq(today))
                .fetch();
    }

    public List<OrderItemDailyFood> findAllWhichGetMembershipBenefit(User user, LocalDateTime now, LocalDateTime threeMonthAgo) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.createdDateTime.between(Timestamp.valueOf(threeMonthAgo), Timestamp.valueOf(now)),
                        orderItemDailyFood.orderStatus.eq(OrderStatus.COMPLETED),
                        orderItemDailyFood.membershipDiscountRate.gt(0))
                .fetch();
    }

    public List<OrderItemDailyFood> findAllMealScheduleByUser(User user) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.dailyFood.serviceDate.goe(LocalDate.now()),
                        orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()))
                .fetch();
    }

    public List<OrderItemDailyFood> findAllByGroupFilter(LocalDate startDate, LocalDate endDate, Group group, List<BigInteger> spotIds, Integer diningTypeCode, BigInteger userId, Makers selectedMakers) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if (startDate != null) {
            whereClause.and(orderItemDailyFood.dailyFood.serviceDate.goe(startDate));
        }

        if (endDate != null) {
            whereClause.and(orderItemDailyFood.dailyFood.serviceDate.loe(endDate));
        }

        if (diningTypeCode != null) {
            whereClause.and(orderItemDailyFood.dailyFood.diningType.eq(DiningType.ofCode(diningTypeCode)));
        }

        if (userId != null) {
            whereClause.and(orderItemDailyFood.order.user.id.eq(userId));
        }

        if (spotIds != null && !spotIds.isEmpty()) {
            whereClause.and(orderDailyFood.spot.id.in(spotIds));
        }

        if (selectedMakers != null) {
            whereClause.and(makers.eq(selectedMakers));
        }

        if (group != null) {
            whereClause.and(orderItemDailyFood.dailyFood.group.eq(group));
        }

        return queryFactory.selectFrom(orderItemDailyFood)
                .innerJoin(orderDailyFood).on(orderItemDailyFood.order.id.eq(orderDailyFood.id))
                .innerJoin(orderItemDailyFood.dailyFood, dailyFood)
                .innerJoin(dailyFood.food, food)
                .innerJoin(food.makers, makers)
                .where(whereClause)
                .orderBy(orderItemDailyFood.dailyFood.serviceDate.desc())
                .fetch();
    }

    public List<OrderItemDailyFood> findAllGroupOrderByFilter(Group group, LocalDate startDate, LocalDate endDate, List<BigInteger> spotIds, Integer diningTypeCode, BigInteger userId) {
        BooleanExpression whereClause = orderItemDailyFood.dailyFood.group.eq(group);

        if (startDate != null) {
            whereClause = whereClause.and(orderItemDailyFood.dailyFood.serviceDate.goe(startDate));
        }

        if (endDate != null) {
            whereClause = whereClause.and(orderItemDailyFood.dailyFood.serviceDate.loe(endDate));
        }

        if (diningTypeCode != null) {
            whereClause = whereClause.and(orderItemDailyFood.dailyFood.diningType.eq(DiningType.ofCode(diningTypeCode)));
        }

        if (userId != null) {
            whereClause = whereClause.and(orderItemDailyFood.order.user.id.eq(userId));
        }

        if (spotIds != null && !spotIds.isEmpty()) {
            whereClause = whereClause.and(orderDailyFood.spot.id.in(spotIds));
        }

        return queryFactory.selectFrom(orderItemDailyFood)
                .innerJoin(orderDailyFood).on(orderItemDailyFood.order.id.eq(orderDailyFood.id))
                .innerJoin(orderItemDailyFood.dailyFood, dailyFood)
                .innerJoin(dailyFood.food, food)
                .innerJoin(food.makers, makers)
                .where(whereClause)
                .orderBy(orderItemDailyFood.dailyFood.serviceDate.desc())
                .fetch();
    }

    public List<OrderItemDailyFood> findAllByMakersFilter(LocalDate startDate, LocalDate endDate, Makers selectedMakers, List<Integer> diningTypeCodes) {
        BooleanExpression whereClause = makers.eq(selectedMakers);

        if (startDate != null) {
            whereClause = whereClause.and(dailyFood.serviceDate.goe(startDate));
        }

        if (endDate != null) {
            whereClause = whereClause.and(dailyFood.serviceDate.loe(endDate));
        }

        if (diningTypeCodes != null && !diningTypeCodes.isEmpty()) {
            List<DiningType> diningTypes = new ArrayList<>();
            for (Integer diningType : diningTypeCodes) {
                diningTypes.add(DiningType.ofCode(diningType));
            }
            whereClause = whereClause.and(dailyFood.diningType.in(diningTypes));
        }

        whereClause = whereClause.and(orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()));

        return queryFactory.selectFrom(orderItemDailyFood)
                .innerJoin(orderDailyFood).on(orderItemDailyFood.order.id.eq(orderDailyFood.id))
                .innerJoin(orderItemDailyFood.dailyFood, dailyFood)
                .innerJoin(dailyFood.food, food)
                .innerJoin(food.makers, makers)
                .where(whereClause)
                .orderBy(orderItemDailyFood.dailyFood.serviceDate.asc())
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

    public List<CapacityDto.MakersCapacity> getMakersCounts(List<DailyFood> selectedDailyFoods) {
        List<CapacityDto.MakersCapacity> makersCapacities = new ArrayList<>();
        List<Makers> selectedMakers = new ArrayList<>();
        List<LocalDate> selectedServiceDate = new ArrayList<>();
        List<DiningType> selectedDiningTypes = new ArrayList<>();

        for (DailyFood selectedDailyFood : selectedDailyFoods) {
            selectedMakers.add(selectedDailyFood.getFood().getMakers());
            selectedServiceDate.add(selectedDailyFood.getServiceDate());
            selectedDiningTypes.add(selectedDailyFood.getDiningType());
        }

        List<Tuple> result = queryFactory.select(orderItemDailyFood.dailyFood.serviceDate,
                        orderItemDailyFood.dailyFood.diningType,
                        food.makers,
                        sum(orderItemDailyFood.count))
                .from(orderItemDailyFood)
                .join(orderItemDailyFood.dailyFood, dailyFood)
                .join(dailyFood.food, food)
                .where(food.makers.in(selectedMakers),
                        dailyFood.serviceDate.in(selectedServiceDate),
                        dailyFood.diningType.in(selectedDiningTypes),
                        orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()))
                .groupBy(orderItemDailyFood.dailyFood.serviceDate,
                        orderItemDailyFood.dailyFood.diningType,
                        food.makers)
                .fetch();

        for (Tuple tuple : result) {
            LocalDate serviceDate = tuple.get(orderItemDailyFood.dailyFood.serviceDate);
            DiningType diningType = tuple.get(orderItemDailyFood.dailyFood.diningType);
            Makers makers = tuple.get(food.makers);
            Integer capacity = tuple.get(sum(orderItemDailyFood.count));
            makersCapacities.add(new CapacityDto.MakersCapacity(serviceDate, diningType, makers, capacity));
        }

        return makersCapacities;
    }


    public List<OrderItemDailyFood> findAllByIds(List<BigInteger> ids) {
        return queryFactory.selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.id.in(ids))
                .fetch();
    }

    public List<OrderItemDailyFood> findAllByServiceDateBetweenStartAndEnd(LocalDate start, LocalDate end) {
        return queryFactory.selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.dailyFood.serviceDate.between(start, end))
                .fetch();
    }

    public List<OrderItemDailyFood> findAllFilterGroup(LocalDate start, LocalDate end, List<Group> groups, List<Spot> spotList) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if (start != null) {
            whereClause.and(dailyFood.serviceDate.goe(start));
        }
        if (end != null) {
            whereClause.and(dailyFood.serviceDate.loe(end));
        }
        if(groups != null && !groups.isEmpty()) {
            whereClause.and(dailyFood.group.in(groups));
        }
        if(spotList != null && !spotList.isEmpty()) {
            whereClause.and(orderDailyFood.spot.in(spotList));
        }

        return queryFactory.selectFrom(orderItemDailyFood)
                .leftJoin(orderItemDailyFood.dailyFood, dailyFood)
                .leftJoin(dailyFood.group, group)
                .leftJoin(orderDailyFood)
                .on(orderItemDailyFood.order.id.eq(orderDailyFood.id))
                .where(whereClause, orderItemDailyFood.orderStatus.eq(OrderStatus.COMPLETED))
                .fetch();
    }



}
