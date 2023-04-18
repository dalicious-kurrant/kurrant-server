package co.dalicious.domain.paycheck.repository;

import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.Coalesce;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static co.dalicious.domain.food.entity.QFood.food;
import static co.dalicious.domain.food.entity.QMakers.makers;
import static co.dalicious.domain.order.entity.QOrderItem.orderItem;
import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;

@Repository
@RequiredArgsConstructor
public class QMakersPaycheckRepository {
    private final JPAQueryFactory queryFactory;

    public List<PaycheckDto.PaycheckDailyFood> getPaycheckDto() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        SimpleExpression<Integer> countSumExpression = orderItemDailyFood.count.sum();
        Coalesce<BigDecimal> supplyPriceCoalesce = new Coalesce<BigDecimal>().add(dailyFood.supplyPrice).add(food.supplyPrice);

        List<Tuple> result = queryFactory.select(makers, dailyFood.serviceDate, dailyFood.diningType, food, food.name, supplyPriceCoalesce, countSumExpression)
                .from(food)
                .leftJoin(dailyFood).on(food.eq(dailyFood.food))
                .leftJoin(orderItemDailyFood).on(orderItemDailyFood.dailyFood.eq(dailyFood))
                .leftJoin(orderItem).on(orderItem.id.eq(orderItemDailyFood.id))
                .leftJoin(makers).on(food.makers.eq(makers))
                .where(dailyFood.serviceDate.goe(startOfMonth), dailyFood.serviceDate.loe(endOfMonth), orderItem.orderStatus.in(OrderStatus.completePayment()))
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

}
