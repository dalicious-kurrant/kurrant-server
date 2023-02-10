package co.dalicious.domain.food.repository;


import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.system.util.enums.DiningType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
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

    public List<DailyFood> findAllByFoodIds(List<BigInteger> foodIds) {
        return queryFactory
                .selectFrom(dailyFood)
                .where(dailyFood.id.in(foodIds))
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
}

//    private BooleanExpression eqCreatedAt(String selectedDate){
//        if(!StringUtils.hasText(selectedDate)) return null;
//        else{
//            LocalDate date = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            return dailyFood.created.between(date.atStartOfDay().toLocalDate(), LocalDateTime.of(date, LocalTime.MAX).toLocalDate());
//        }

