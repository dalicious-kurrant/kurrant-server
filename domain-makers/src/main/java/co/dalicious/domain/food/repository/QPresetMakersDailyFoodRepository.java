package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.PresetMakersDailyFood;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

import static co.dalicious.domain.food.entity.QPresetMakersDailyFood.presetMakersDailyFood;

@Repository
@RequiredArgsConstructor
public class QPresetMakersDailyFoodRepository {
    private final JPAQueryFactory queryFactory;

    public PresetMakersDailyFood findByIdAndMakers(BigInteger scheduleId, Makers makers) {
        return queryFactory.selectFrom(presetMakersDailyFood)
                .where(presetMakersDailyFood.id.eq(scheduleId), presetMakersDailyFood.makers.eq(makers))
                .fetchOne();
    }

    public List<PresetMakersDailyFood> getMostRecentPresets(Integer page) {
        StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , presetMakersDailyFood.createdDateTime
                , ConstantImpl.create("yyyy-MM-dd"));

        List<PresetMakersDailyFood> dates = queryFactory.selectFrom(presetMakersDailyFood)
                .groupBy(formattedDate, presetMakersDailyFood.createdDateTime)
                .orderBy(presetMakersDailyFood.createdDateTime.desc())
                .limit(2)
                .fetch();

        String startDate = null;
        String endDate = null;

        //페이지가 1페이지면 가장 최근 날짜
        if(dates != null && dates.size() != 0 && page == 1) {
            startDate = dates.get(0).getCreatedDateTime() + "00:00:00";
            endDate = dates.get(0).getCreatedDateTime() + "23:59:59";
            return queryFactory.selectFrom(presetMakersDailyFood)
                    .where(presetMakersDailyFood.createdDateTime.between(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate)))
                    .fetch();
        }
        //페이지가 2페이지면 그 전 날짜
        if(dates != null && dates.size() != 0 && page == 2) {
            startDate = dates.get(0).getCreatedDateTime() + "00:00:00";
            endDate = dates.get(0).getCreatedDateTime() + "23:59:59";
            return queryFactory.selectFrom(presetMakersDailyFood)
                    .where(presetMakersDailyFood.createdDateTime.between(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate)))
                    .fetch();
        }

        throw new ApiException(ExceptionEnum.NOT_FOUND);
    }

}
