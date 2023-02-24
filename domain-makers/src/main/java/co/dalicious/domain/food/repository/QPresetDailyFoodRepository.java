package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.PresetDailyFood;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
import co.dalicious.domain.food.util.QuerydslDateFormatUtils;
import co.dalicious.system.util.DateUtils;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

//    public static StringTemplate getStringTemplate() {
//        StringTemplate formattedDate = Expressions.stringTemplate(
//                "DATE_FORMAT({0}, {1})"
//                , presetDailyFood.createdDateTime
//                , ConstantImpl.create("%Y-%m-%d"));
//        return formattedDate;
//    }
    public List<PresetDailyFood> getApprovedPresetDailyFoodBetweenServiceDate(LocalDate startDate, LocalDate endDate) {
        return queryFactory.selectFrom(presetDailyFood)
                .where(presetDailyFood.scheduleStatus.eq(ScheduleStatus.APPROVAL),
                        presetDailyFood.presetGroupDailyFood.presetMakersDailyFood.serviceDate.goe(startDate),
                        presetDailyFood.presetGroupDailyFood.presetMakersDailyFood.serviceDate.loe(endDate))
                .fetch();
    }
}
