package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.PresetDailyFood;
import co.dalicious.domain.food.entity.PresetGroupDailyFood;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.food.entity.QPresetDailyFood.presetDailyFood;

@Repository
@RequiredArgsConstructor
public class QPresetDailyFoodRepository{
    private final JPAQueryFactory queryFactory;

    public PresetDailyFood findById(BigInteger id){
        return queryFactory.selectFrom(presetDailyFood)
                .where(presetDailyFood.id.eq(id))
                .fetchOne();
    }

    public List<PresetDailyFood> getApprovedPresetDailyFoodBetweenServiceDate(LocalDate startDate, LocalDate endDate) {
        return queryFactory.selectFrom(presetDailyFood)
                .where(presetDailyFood.scheduleStatus.eq(ScheduleStatus.APPROVAL),
                        presetDailyFood.presetGroupDailyFood.presetMakersDailyFood.serviceDate.goe(startDate),
                        presetDailyFood.presetGroupDailyFood.presetMakersDailyFood.serviceDate.loe(endDate))
                .fetch();
    }

    public List<PresetDailyFood> getAllAndPresetGroupDailyFood(List<PresetGroupDailyFood> presetGroupDailyFoodList) {
        return queryFactory.selectFrom(presetDailyFood)
                .where(presetDailyFood.presetGroupDailyFood.in(presetGroupDailyFoodList))
                .fetch();
    }
}
