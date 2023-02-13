package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.FoodSchedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static co.dalicious.domain.food.entity.QFoodSchedule.foodSchedule;

@Repository
@RequiredArgsConstructor
public class QFoodScheduleRepository {
    public final JPAQueryFactory queryFactory;

    public List<FoodSchedule> findAllByDailyFoods(List<DailyFood> dailyFoods) {
        return dailyFoods.stream()
                .map(dailyFood -> queryFactory.selectFrom(foodSchedule)
                        .where(foodSchedule.food.eq(dailyFood.getFood()),
                                foodSchedule.serviceDate.eq(dailyFood.getServiceDate()),
                                foodSchedule.diningType.eq(dailyFood.getDiningType()))
                        .fetchOne())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public FoodSchedule findOneByDailyFood(DailyFood dailyFood) {
        return queryFactory.selectFrom(foodSchedule)
                .where(foodSchedule.food.eq(dailyFood.getFood()),
                        foodSchedule.serviceDate.eq(dailyFood.getServiceDate()),
                        foodSchedule.diningType.eq(dailyFood.getDiningType()))
                .fetchOne();
    }
}
