package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.MakersSchedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static co.dalicious.domain.food.entity.QMakersSchedule.makersSchedule;


@Component
@RequiredArgsConstructor
public class QMakersScheduleRepository {
    private final JPAQueryFactory queryFactory;

    public List<MakersSchedule> findAllByDailyFoods(List<DailyFood> dailyFoods) {
        return dailyFoods.stream()
                .map(dailyFood -> queryFactory.selectFrom(makersSchedule)
                        .where(makersSchedule.serviceDate.eq(dailyFood.getServiceDate()),
                                makersSchedule.diningType.eq(dailyFood.getDiningType()))
                        .fetchOne())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public MakersSchedule findOneByDailyFood(DailyFood dailyFood) {
        return queryFactory.selectFrom(makersSchedule)
                .where(makersSchedule.serviceDate.eq(dailyFood.getServiceDate()),
                        makersSchedule.diningType.eq(dailyFood.getDiningType()))
                .fetchOne();
    }
}
