package co.dalicious.domain.food.repository;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.PresetDailyFood;
import co.dalicious.domain.food.entity.PresetMakersDailyFood;
import co.dalicious.domain.food.entity.enums.ConfirmStatus;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.food.entity.QFood.food;
import static co.dalicious.domain.food.entity.QMakers.makers;
import static co.dalicious.domain.food.entity.QPresetDailyFood.presetDailyFood;
import static co.dalicious.domain.food.entity.QPresetMakersDailyFood.presetMakersDailyFood;


@Repository
@RequiredArgsConstructor
public class QPresetMakersDailyFoodRepository {
    private final JPAQueryFactory queryFactory;

    public List<PresetMakersDailyFood> findByServiceDateAndConfirmStatus() {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));

        return queryFactory.selectFrom(presetMakersDailyFood)
                .where(presetMakersDailyFood.serviceDate.after(now),
                        presetMakersDailyFood.confirmStatus.ne(ConfirmStatus.COMPLETE))
                .fetch();
    }

    public Page<PresetMakersDailyFood> findAllServiceDateAndConfirmStatusAndFilter(Makers makers, ScheduleStatus scheduleStatus, Pageable pageable, Integer size, Integer page) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if(makers != null) {
            whereClause.and(presetMakersDailyFood.makers.eq(makers));
        }
        if(scheduleStatus != null) {
            whereClause.and(presetMakersDailyFood.scheduleStatus.eq(scheduleStatus));
        }

        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int itemLimit = size * page;
        int itemOffset = size * (page - 1);

        QueryResults<PresetMakersDailyFood> results =
                queryFactory.selectFrom(presetMakersDailyFood)
                        .where(presetMakersDailyFood.serviceDate.after(now), presetMakersDailyFood.confirmStatus.ne(ConfirmStatus.COMPLETE), whereClause)
                        .orderBy(presetMakersDailyFood.serviceDate.asc())
                        .limit(itemLimit)
                        .offset(itemOffset)
                        .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());

    }

    public PresetMakersDailyFood findByMakersAndServiceDateAndDiningType(Makers makers, LocalDate serviceDate, DiningType diningType) {
        return queryFactory.selectFrom(presetMakersDailyFood)
                .where(presetMakersDailyFood.makers.eq(makers),
                        presetMakersDailyFood.serviceDate.eq(serviceDate),
                        presetMakersDailyFood.diningType.eq(diningType))
                .fetchOne();
    }

    public PresetMakersDailyFood findByIdAndMakers(BigInteger presetMakersId, Makers makers) {
        return queryFactory.selectFrom(presetMakersDailyFood)
                .where(presetMakersDailyFood.id.eq(presetMakersId), presetMakersDailyFood.makers.eq(makers))
                .fetchOne();
    }

    public List<PresetMakersDailyFood> getMostRecentPresets(Integer page, Makers makers) {
        StringTemplate formattedDate = getStringTemplate();

        List<String> dates = queryFactory
                .select(formattedDate)
                .from(presetMakersDailyFood)
                .groupBy(formattedDate)
                .orderBy(formattedDate.desc())
                .limit(2)
                .fetch();

        if(dates != null && page <= dates.size()){
            LocalDate date = DateUtils.stringToDate(dates.get(page-1));
            LocalDateTime startDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59);
            return queryFactory.selectFrom(presetMakersDailyFood)
                    .where(presetMakersDailyFood.makers.eq(makers),
                            presetMakersDailyFood.createdDateTime.between(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate)),
                            presetMakersDailyFood.scheduleStatus.ne(ScheduleStatus.REJECTED))
                    .fetch();
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND);
    }

    public static StringTemplate getStringTemplate() {
        StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , presetMakersDailyFood.createdDateTime
                , ConstantImpl.create("%Y-%m-%d"));
        return formattedDate;
    }

}
