package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.client.entity.MealInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

import static co.dalicious.domain.client.entity.QMealInfo.mealInfo;

@Repository
@RequiredArgsConstructor
public class QMealInfoRepository {

    private final JPAQueryFactory queryFactory;

    public List<DayAndTime> getMealInfoLastOrderTime() {
        return queryFactory.select(mealInfo.lastOrderTime)
                .from(mealInfo)
                .where(mealInfo.lastOrderTime.isNotNull())
                .distinct()
                .fetch();
    }
}
