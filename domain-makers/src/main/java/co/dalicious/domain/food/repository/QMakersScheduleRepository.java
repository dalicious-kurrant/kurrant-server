package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.FoodSchedule;
import co.dalicious.domain.food.entity.MakersSchedule;
import co.dalicious.system.enums.DiningType;
import com.mysema.commons.lang.Pair;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static co.dalicious.domain.food.entity.QFoodSchedule.foodSchedule;
import static co.dalicious.domain.food.entity.QMakersSchedule.makersSchedule;


@Component
@RequiredArgsConstructor
public class QMakersScheduleRepository {
    private final JPAQueryFactory queryFactory;

    public List<MakersSchedule> findAllByDailyFoods(List<DailyFood> dailyFoods) {
        List<Pair<LocalDate, DiningType>> keys = dailyFoods.stream()
                .map(df -> Pair.of(df.getServiceDate(), df.getDiningType()))
                .distinct()
                .toList();

        List<MakersSchedule> makersSchedules = queryFactory.selectFrom(makersSchedule)
                .where(makersSchedule.serviceDate.in(keys.stream().map(Pair::getFirst).collect(Collectors.toList())),
                        makersSchedule.diningType.in(keys.stream().map(Pair::getSecond).collect(Collectors.toList())))
                .fetch();

        Map<Pair<LocalDate, DiningType>, MakersSchedule> makersScheduleMap = makersSchedules.stream()
                .collect(Collectors.toMap(ms -> Pair.of(ms.getServiceDate(), ms.getDiningType()), Function.identity()));

        return keys.stream()
                .map(makersScheduleMap::get)
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
