package co.dalicious.domain.food.repository;


import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.OpenGroup;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.dto.DeliveryInfoDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static co.dalicious.domain.client.entity.QGroup.group;
import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.food.entity.QFood.food;
import static co.dalicious.domain.food.entity.QMakers.makers;
import static co.dalicious.domain.food.entity.embebbed.QDeliverySchedule.deliverySchedule;

@Repository
@RequiredArgsConstructor
public class QDailyFoodRepository {

    public final JPAQueryFactory queryFactory;

    public List<DailyFood> getSellingAndSoldOutDailyFood(Group group, LocalDate selectedDate) {
        return queryFactory
                .selectFrom(dailyFood)
                .where(dailyFood.group.eq(group),
                        dailyFood.serviceDate.eq(selectedDate),
                        dailyFood.dailyFoodStatus.in(DailyFoodStatus.SALES, DailyFoodStatus.SOLD_OUT, DailyFoodStatus.PASS_LAST_ORDER_TIME))
                .fetch();
    }

    public List<DailyFood> getSellingDailyFoodsBetweenServiceDate(LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(dailyFood)
                .where(dailyFood.serviceDate.goe(startDate),
                        dailyFood.serviceDate.loe(endDate),
                        dailyFood.dailyFoodStatus.eq(DailyFoodStatus.SALES))
                .fetch();
    }

    public List<DailyFood> getDailyFoodsBetweenServiceDate(LocalDate startDate, LocalDate endDate, Group group) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(group != null) {
            whereCause.and(dailyFood.group.eq(group));
        }

        return queryFactory
                .selectFrom(dailyFood)
                .where(dailyFood.serviceDate.goe(startDate),
                        dailyFood.serviceDate.loe(endDate),
                        dailyFood.dailyFoodStatus.in(DailyFoodStatus.SALES, DailyFoodStatus.SOLD_OUT, DailyFoodStatus.PASS_LAST_ORDER_TIME),
                        whereCause)
                .fetch();
    }

    public List<DailyFood> findAllByDailyFoodIds(List<BigInteger> dailyFoodIds) {
        return queryFactory
                .selectFrom(dailyFood)
                .where(dailyFood.id.in(dailyFoodIds))
                .fetch();
    }

    public List<DailyFood> findAllByFoodIds(Set<BigInteger> foodIds) {
        return queryFactory
                .selectFrom(dailyFood)
                .where(dailyFood.food.id.in(foodIds))
                .fetch();
    }

    public List<DailyFood> findAllByGroupAndSelectedDateAndDiningType(Group group, LocalDate selectedDate, DiningType diningType) {
        return queryFactory
                .selectFrom(dailyFood)
                .where(dailyFood.group.eq(group),
                        dailyFood.serviceDate.eq(selectedDate),
                        dailyFood.diningType.eq(diningType),
                        dailyFood.dailyFoodStatus.in(DailyFoodStatus.SALES)
                )
                .fetch();
    }

    public List<DailyFood> findAllByMakersAndServiceDateAndDiningType(Makers makers, LocalDate serviceDate, DiningType diningType) {
        return queryFactory
                .selectFrom(dailyFood)
                .where(dailyFood.food.makers.eq(makers),
                        dailyFood.serviceDate.eq(serviceDate),
                        dailyFood.diningType.eq(diningType))
                .fetch();
    }

    public List<DailyFood> findAllByGroupAndMakersBetweenServiceDate(LocalDate startDate, LocalDate endDate, List<BigInteger> groupIds, List<BigInteger> makersIds) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if (startDate != null) {
            whereClause.and(dailyFood.serviceDate.goe(startDate));
        }

        if (endDate != null) {
            whereClause.and(dailyFood.serviceDate.loe(endDate));
        }

        if (groupIds != null && !groupIds.isEmpty()) {
            whereClause.and(dailyFood.group.id.in(groupIds));
        }

        if (makersIds != null && !makersIds.isEmpty()) {
            whereClause.and(dailyFood.food.makers.id.in(makersIds));
        }

        return queryFactory.selectFrom(dailyFood)
                .where(whereClause)
                .orderBy(dailyFood.serviceDate.asc())
                .fetch();
    }

    public List<DailyFood> findAllByFoodsBetweenServiceDate(LocalDate startDate, LocalDate endDate, Set<BigInteger> foodIds) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if (startDate != null) {
            whereClause.and(dailyFood.serviceDate.goe(startDate));
        }

        if (endDate != null) {
            whereClause.and(dailyFood.serviceDate.loe(endDate));
        }

        if (foodIds != null && !foodIds.isEmpty()) {
            whereClause.and(dailyFood.food.id.in(foodIds));
        }

        return queryFactory.selectFrom(dailyFood)
                .where(whereClause)
                .orderBy(dailyFood.serviceDate.asc())
                .fetch();
    }

    public List<DailyFood> findAllDailyFoodByMakersBetweenServiceDate(LocalDate startDate, LocalDate endDate, Makers makers) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if (startDate != null) {
            whereClause.and(dailyFood.serviceDate.goe(startDate));
        }
        if (endDate != null) {
            whereClause.and(dailyFood.serviceDate.loe(endDate));
        }

        return queryFactory.selectFrom(dailyFood)
                .where(whereClause, dailyFood.food.makers.eq(makers))
                .fetch();
    }

    public List<DailyFood> findAllFilterGroupAndSpot(LocalDate start, LocalDate end, List<Group> groups, List<Spot> spotList) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if (start != null) {
            whereClause.and(dailyFood.serviceDate.goe(start));
        }
        if (end != null) {
            whereClause.and(dailyFood.serviceDate.loe(end));
        }
        if (groups != null && !groups.isEmpty()) {
            whereClause.and(group.in(groups));
        }
        if (spotList != null && !spotList.isEmpty()) {
            whereClause.and(group.spots.any().in(spotList));
        }

        return queryFactory.selectFrom(dailyFood)
                .leftJoin(dailyFood.group, group)
                .where(whereClause, group.instanceOf(Corporation.class).or(group.instanceOf(OpenGroup.class)))
                .distinct()
                .fetch();
    }

    public List<DeliveryInfoDto> groupingByServiceDateAndRoute(LocalDate startDate, LocalDate endDate) {
        List<Tuple> tuples = queryFactory.select(dailyFood.serviceDate, dailyFood.diningType, group, makers, deliverySchedule.deliveryTime)
                .from(dailyFood)
                .leftJoin(dailyFood.group, group)
                .leftJoin(dailyFood.food, food)
                .leftJoin(food.makers, makers)
                .rightJoin(dailyFood.dailyFoodGroup.deliverySchedules, deliverySchedule)
                .where(dailyFood.serviceDate.between(startDate, endDate))
                .groupBy(dailyFood.serviceDate, dailyFood.diningType, group.id, makers.id, deliverySchedule.deliveryTime)
                .orderBy(dailyFood.serviceDate.asc(), dailyFood.diningType.asc(), group.id.asc(), makers.id.asc(), deliverySchedule.deliveryTime.asc())
                .fetch();

        return tuples.parallelStream()
                .map(tuple -> new DeliveryInfoDto(
                        tuple.get(dailyFood.serviceDate),
                        tuple.get(dailyFood.diningType),
                        tuple.get(group),
                        tuple.get(makers),
                        tuple.get(deliverySchedule.deliveryTime)
                ))
                .toList();
    }
}

