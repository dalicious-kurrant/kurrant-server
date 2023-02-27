package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Makers;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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

    public Page<PresetMakersDailyFood> findAllServiceDateAndConfirmStatusAndFilter(List<BigInteger> makersIds, List<Integer> scheduleStatusList, Pageable pageable, Integer size, Integer page) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if(makersIds != null && !makersIds.isEmpty()) {
            whereClause.and(presetMakersDailyFood.makers.id.in(makersIds));
        }
        if(scheduleStatusList != null && !scheduleStatusList.isEmpty()) {
            List<ScheduleStatus> scheduleStatus = new ArrayList<>();
            scheduleStatusList.forEach(status -> scheduleStatus.add(ScheduleStatus.ofCode(status)));
            whereClause.and(presetMakersDailyFood.scheduleStatus.in(scheduleStatus));
        }

        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int itemLimit = size * page;
        int itemOffset = size * (page - 1);

        QueryResults<PresetMakersDailyFood> results =
                queryFactory.selectFrom(presetMakersDailyFood)
                        .where(whereClause, presetMakersDailyFood.serviceDate.after(now), presetMakersDailyFood.confirmStatus.ne(ConfirmStatus.COMPLETE))
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
                            presetMakersDailyFood.scheduleStatus.ne(ScheduleStatus.REJECTED),
                            presetMakersDailyFood.confirmStatus.eq(ConfirmStatus.REQUEST))
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
