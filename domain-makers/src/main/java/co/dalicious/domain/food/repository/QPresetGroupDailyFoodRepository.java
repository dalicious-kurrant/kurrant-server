package co.dalicious.domain.food.repository;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.PresetGroupDailyFood;
import co.dalicious.domain.food.entity.PresetMakersDailyFood;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static co.dalicious.domain.food.entity.QPresetGroupDailyFood.presetGroupDailyFood;

@Repository
@RequiredArgsConstructor
public class QPresetGroupDailyFoodRepository {
    private final JPAQueryFactory queryFactory;

    public PresetGroupDailyFood findByGroupAndMakersDailyFood(Group group, PresetMakersDailyFood presetMakersDailyFood) {
        return queryFactory.selectFrom(presetGroupDailyFood)
                .where(presetGroupDailyFood.group.eq(group),
                        presetGroupDailyFood.presetMakersDailyFood.eq(presetMakersDailyFood))
                .fetchOne();
    }
}
