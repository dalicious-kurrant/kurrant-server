package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodSchedule;
import co.dalicious.system.enums.DiningType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static co.dalicious.domain.food.entity.QFoodSchedule.foodSchedule;

@Repository
@RequiredArgsConstructor
public class QFoodScheduleRepository {
    public final JPAQueryFactory queryFactory;

    public List<FoodSchedule> findAllByDailyFoods(List<DailyFood> dailyFoods) {
        Set<Food> foods = dailyFoods.stream().map(DailyFood::getFood).collect(Collectors.toSet());
        Set<LocalDate> serviceDates = dailyFoods.stream().map(DailyFood::getServiceDate).collect(Collectors.toSet());
        Set<DiningType> diningTypes = dailyFoods.stream().map(DailyFood::getDiningType).collect(Collectors.toSet());

        return queryFactory.selectFrom(foodSchedule)
                .where(foodSchedule.food.in(foods),
                        foodSchedule.serviceDate.in(serviceDates),
                        foodSchedule.diningType.in(diningTypes))
                .fetch();
    }

    public FoodSchedule findOneByDailyFood(DailyFood dailyFood) {
        return queryFactory.selectFrom(foodSchedule)
                .where(foodSchedule.food.eq(dailyFood.getFood()),
                        foodSchedule.serviceDate.eq(dailyFood.getServiceDate()),
                        foodSchedule.diningType.eq(dailyFood.getDiningType()))
                .fetchOne();
    }
}
