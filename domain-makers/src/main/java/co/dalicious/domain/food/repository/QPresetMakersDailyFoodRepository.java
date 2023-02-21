package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.PresetMakersDailyFood;
import co.dalicious.system.util.DateUtils;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static co.dalicious.domain.food.entity.QPresetMakersDailyFood.presetMakersDailyFood;
import static java.time.ZoneId.of;

@Repository
@RequiredArgsConstructor
public class QPresetMakersDailyFoodRepository {
    private final JPAQueryFactory queryFactory;

    public PresetMakersDailyFood findByIdAndMakers(BigInteger scheduleId, Makers makers) {
        return queryFactory.selectFrom(presetMakersDailyFood)
                .where(presetMakersDailyFood.id.eq(scheduleId), presetMakersDailyFood.makers.eq(makers))
                .fetchOne();
    }

    public List<PresetMakersDailyFood> getMostRecentPresets(Integer page, Makers makers) {
        StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , presetMakersDailyFood.createdDateTime
                , ConstantImpl.create("%Y-%m-%d"));

        List<String> dates = queryFactory
                .select(formattedDate)
                .from(presetMakersDailyFood)
                .groupBy(formattedDate)
                .orderBy(formattedDate.desc())
                .limit(2)
                .fetch();

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        //페이지가 1페이지면 가장 최근 날짜
        if(dates.size() != 0 && page == 1) {
            LocalDate date = DateUtils.stringToDate(dates.get(0));
            startDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
            endDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59);
            return queryFactory.selectFrom(presetMakersDailyFood)
                    .where(presetMakersDailyFood.makers.eq(makers),
                            presetMakersDailyFood.createdDateTime.between(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate)))
                    .fetch();
        }

        //페이지가 2페이지면 그 전 날짜
//        if(dates.size() < 2) throw new ApiException(ExceptionEnum.NOT_FOUND);
//        if(page == 2) {
//            String date = String.valueOf(dates.get(1)).substring(0, 10);
//            startDate = dates.get(1) + "00:00:00.000000";
//            endDate = dates.get(1) + "23:59:59.999999";
//            return queryFactory.selectFrom(presetMakersDailyFood)
//                    .where(presetMakersDailyFood.makers.eq(makers),
//                            presetMakersDailyFood.createdDateTime.between(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate)))
//                    .fetch();
//        }

        throw new ApiException(ExceptionEnum.NOT_FOUND);
    }

}
