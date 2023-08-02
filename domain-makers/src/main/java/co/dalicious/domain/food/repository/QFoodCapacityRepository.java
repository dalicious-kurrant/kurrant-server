package co.dalicious.domain.food.repository;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.food.entity.FoodCapacity;
import co.dalicious.domain.food.entity.Makers;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.food.entity.QFoodCapacity.foodCapacity;
import static co.dalicious.domain.food.entity.QMakers.makers;

@Repository
@RequiredArgsConstructor
public class QFoodCapacityRepository {
    private final JPAQueryFactory queryFactory;

    public List<DayAndTime> getFoodCapacityLastOrderTime() {
        return queryFactory.select(foodCapacity.lastOrderTime)
                .from(foodCapacity)
                .where(foodCapacity.lastOrderTime.isNotNull())
                .distinct()
                .fetch();
    }

    public List<FoodCapacity> getFoodCapacitiesByMakers(Makers makers) {
        return  queryFactory.selectFrom(foodCapacity)
                .where(foodCapacity.food.makers.eq(makers))
                .fetch();
    }
}
