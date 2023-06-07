package co.dalicious.domain.paycheck.repository;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Coalesce;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static co.dalicious.domain.food.entity.QFood.food;
import static co.dalicious.domain.food.entity.QMakers.makers;
import static co.dalicious.domain.order.entity.QOrderItem.orderItem;
import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;
import static co.dalicious.domain.paycheck.entity.QMakersPaycheck.makersPaycheck;

@Repository
@RequiredArgsConstructor
public class QMakersPaycheckRepository {
    private final JPAQueryFactory queryFactory;

    public List<PaycheckDto.PaycheckDailyFood> getPaycheckDto(YearMonth yearMonth, List<BigInteger> makersIds) {
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        SimpleExpression<Integer> countSumExpression = orderItemDailyFood.count.sum();
        Coalesce<BigDecimal> supplyPriceCoalesce = new Coalesce<BigDecimal>().add(dailyFood.supplyPrice).add(food.supplyPrice);

        BooleanBuilder whereClause = new BooleanBuilder();

        if(makersIds != null && !makersIds.isEmpty()) {
            whereClause.and(makers.id.in(makersIds));
        }

        List<Tuple> result = queryFactory.select(makers, dailyFood.serviceDate, dailyFood.diningType, food, food.name, supplyPriceCoalesce, countSumExpression)
                .from(food)
                .leftJoin(dailyFood).on(food.eq(dailyFood.food))
                .leftJoin(orderItemDailyFood).on(orderItemDailyFood.dailyFood.eq(dailyFood))
                .leftJoin(orderItem).on(orderItem.id.eq(orderItemDailyFood.id))
                .leftJoin(makers).on(food.makers.eq(makers))
                .where(dailyFood.serviceDate.goe(startOfMonth),
                        dailyFood.serviceDate.loe(endOfMonth),
                        orderItem.orderStatus.in(OrderStatus.completePayment()),
                        whereClause)
                .groupBy(makers.id, dailyFood.serviceDate, dailyFood.diningType, food.id)
                .having(countSumExpression.isNotNull())
                .orderBy(makers.id.asc(), dailyFood.serviceDate.asc(), dailyFood.diningType.asc(), food.id.asc())
                .fetch();

        List<PaycheckDto.PaycheckDailyFood> paycheckDailyFoods = new ArrayList<>();

        for (Tuple row : result) {
            PaycheckDto.PaycheckDailyFood paycheckDailyFood = new PaycheckDto.PaycheckDailyFood();
            paycheckDailyFood.setMakers(row.get(makers));
            paycheckDailyFood.setServiceDate(row.get(dailyFood.serviceDate));
            paycheckDailyFood.setDiningType(row.get(dailyFood.diningType));
            paycheckDailyFood.setFood(row.get(food));
            paycheckDailyFood.setFoodName(row.get(food.name));
            paycheckDailyFood.setSupplyPrice(row.get(supplyPriceCoalesce));
            paycheckDailyFood.setCount(row.get(countSumExpression));

            paycheckDailyFoods.add(paycheckDailyFood);
        }

        return paycheckDailyFoods;
    }

    public List<MakersPaycheck> getMakersPaychecksByFilter(YearMonth startYearMonth, YearMonth endYearMonth, List<BigInteger> makersIds, PaycheckStatus paycheckStatus, Boolean hasRequest) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if(startYearMonth != null) {
            whereClause.and(makersPaycheck.yearMonth.goe(startYearMonth));
        }
        if(endYearMonth != null) {
            whereClause.and(makersPaycheck.yearMonth.loe(endYearMonth));
        }
        if(makersIds != null && !makersIds.isEmpty()) {
            whereClause.and(makersPaycheck.makers.id.in(makersIds));
        }
        if(paycheckStatus != null) {
            whereClause.and(makersPaycheck.paycheckStatus.eq(paycheckStatus));
        }
        if(hasRequest != null) {
            whereClause.and(hasRequest ? makersPaycheck.paycheckMemos.isNotEmpty() : makersPaycheck.paycheckMemos.isEmpty());
        }
        return queryFactory.selectFrom(makersPaycheck)
                .where(whereClause)
                .orderBy(makersPaycheck.createdDateTime.asc())
                .fetch();
    }

    public List<MakersPaycheck> getMakersPaychecksByFilter(Makers makers, YearMonth startYearMonth, YearMonth endYearMonth, PaycheckStatus paycheckStatus, Boolean hasRequest) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(makersPaycheck.makers.eq(makers));

        if(startYearMonth != null) {
            whereClause.and(makersPaycheck.yearMonth.goe(startYearMonth));
        }
        if(endYearMonth != null) {
            whereClause.and(makersPaycheck.yearMonth.loe(endYearMonth));
        }

        if(paycheckStatus != null) {
            whereClause.and(makersPaycheck.paycheckStatus.eq(paycheckStatus));
        }
        if(hasRequest != null) {
            whereClause.and(hasRequest ? makersPaycheck.paycheckMemos.isNotEmpty() : makersPaycheck.paycheckMemos.isEmpty());
        }
        return queryFactory.selectFrom(makersPaycheck)
                .where(whereClause)
                .orderBy(makersPaycheck.createdDateTime.asc())
                .fetch();
    }

    public List<MakersPaycheck> getMakersPaychecksByFilter(List<BigInteger> makersIds, YearMonth yearMonth) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if(makersIds != null && !makersIds.isEmpty()) {
            whereClause.and(makersPaycheck.makers.id.in(makersIds));
        }
        return queryFactory.selectFrom(makersPaycheck)
                .where(makersPaycheck.yearMonth.eq(yearMonth), whereClause)
                .fetch();
    }
}
