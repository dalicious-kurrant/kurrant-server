package co.dalicious.domain.food.repository;


import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;

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

    public List<DailyFood> findAllByDailyFoodIds(List<BigInteger> dailyFoodIds) {
        return queryFactory
                .selectFrom(dailyFood)
                .where(dailyFood.id.in(dailyFoodIds))
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
}

