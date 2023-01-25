package co.dalicious.domain.food.repository;


import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.system.util.enums.FoodStatus;
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

    public List<DailyFood> getSellingAndSoldOutDailyFood(BigInteger spotId, LocalDate selectedDate) {
        return queryFactory
                .selectFrom(dailyFood)
                .where(dailyFood.spot.id.eq(spotId),
                        dailyFood.serviceDate.eq(selectedDate),
                        dailyFood.foodStatus.eq(FoodStatus.SALES).or(dailyFood.foodStatus.eq(FoodStatus.SOLD_OUT)))
                .fetch();
    }

    public List<DailyFood> findAllByFoodIds(List<BigInteger> foodIds) {
        return queryFactory
                .selectFrom(dailyFood)
                .where(dailyFood.food.id.in(foodIds))
                .fetch();
    }
}

//    private BooleanExpression eqCreatedAt(String selectedDate){
//        if(!StringUtils.hasText(selectedDate)) return null;
//        else{
//            LocalDate date = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            return dailyFood.created.between(date.atStartOfDay().toLocalDate(), LocalDateTime.of(date, LocalTime.MAX).toLocalDate());
//        }

