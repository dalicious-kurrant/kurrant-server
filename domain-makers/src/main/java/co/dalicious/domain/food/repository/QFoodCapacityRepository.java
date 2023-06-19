package co.dalicious.domain.food.repository;

import co.dalicious.domain.client.entity.DayAndTime;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.food.entity.QFoodCapacity.foodCapacity;

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
}
