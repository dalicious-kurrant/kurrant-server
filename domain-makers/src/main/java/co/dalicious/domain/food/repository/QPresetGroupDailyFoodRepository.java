package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.PresetGroupDailyFood;
import co.dalicious.domain.food.entity.PresetMakersDailyFood;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static co.dalicious.domain.food.entity.QPresetGroupDailyFood.presetGroupDailyFood;

@Repository
@RequiredArgsConstructor
public class QPresetGroupDailyFoodRepository {
    private final JPAQueryFactory queryFactory;

    public List<PresetGroupDailyFood> findAllAndPresetMakersDailyFood(List<PresetMakersDailyFood> presetMakersDailyFoodList) {
        return queryFactory.selectFrom(presetGroupDailyFood)
                .where(presetGroupDailyFood.presetMakersDailyFood.in(presetMakersDailyFoodList))
                .fetch();
    }
}
