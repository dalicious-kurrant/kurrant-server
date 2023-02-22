package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.PresetDailyFood;
import co.dalicious.domain.food.util.QuerydslDateFormatUtils;
import co.dalicious.system.util.DateUtils;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static co.dalicious.domain.food.entity.QPresetDailyFood.presetDailyFood;
import static co.dalicious.domain.food.entity.QPresetMakersDailyFood.presetMakersDailyFood;

@Repository
@RequiredArgsConstructor
public class QPresetDailyFoodRepository{
    private final JPAQueryFactory queryFactory;

    public PresetDailyFood findById(BigInteger id){
        return queryFactory.selectFrom(presetDailyFood)
                .where(presetDailyFood.id.eq(id))
                .fetchOne();
    }

    public List<PresetDailyFood> findAllByCreatedDate() {
        StringTemplate formattedDate = QuerydslDateFormatUtils.getStringTemplateByTimestamp(presetDailyFood.createdDateTime);

        String dates = queryFactory
                .select(formattedDate)
                .from(presetMakersDailyFood)
                .groupBy(formattedDate)
                .orderBy(formattedDate.desc())
                .limit(1)
                .fetchOne();

        if (dates != null) {
            LocalDate date = DateUtils.stringToDate(dates);
            LocalDateTime startDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59);

            return queryFactory.selectFrom(presetDailyFood)
                    .where(presetDailyFood.createdDateTime.between(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate)))
                    .fetch();
        }

        return null;
    }

    public static StringTemplate getStringTemplate() {
        StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , presetDailyFood.createdDateTime
                , ConstantImpl.create("%Y-%m-%d"));
        return formattedDate;
    }
}
