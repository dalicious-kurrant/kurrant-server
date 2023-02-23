package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.PresetMakersDailyFood;
import co.dalicious.domain.food.entity.enums.ConfirmStatus;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static co.dalicious.domain.food.entity.QPresetMakersDailyFood.presetMakersDailyFood;


@Repository
@RequiredArgsConstructor
public class QPresetMakersDailyFoodRepository {
    private final JPAQueryFactory queryFactory;

    public List<PresetMakersDailyFood> findByServiceDateAndConfirmStatus() {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // 가장 마지막 서비스 데이터를 가져오기
        LocalDate lastServiceDate = queryFactory
                .select(presetMakersDailyFood.serviceDate).from(presetMakersDailyFood)
                .groupBy(presetMakersDailyFood.serviceDate)
                .orderBy(presetMakersDailyFood.serviceDate.desc())
                .limit(1).fetchOne();

        System.out.println("lastServiceDate = " + lastServiceDate);

        if(lastServiceDate != null) {
            return queryFactory.selectFrom(presetMakersDailyFood)
                    .where(presetMakersDailyFood.serviceDate.between(now, lastServiceDate),
                            presetMakersDailyFood.confirmStatus.eq(ConfirmStatus.REQUEST))
                    .fetch();

        }

        throw new ApiException(ExceptionEnum.NOT_FOUND);
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



    public List<PresetMakersDailyFood> getMostRecentPresetsInDeadline() {
        StringTemplate formattedDate = getStringTemplate();

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        String dates = queryFactory
                .select(formattedDate)
                .from(presetMakersDailyFood)
                .groupBy(formattedDate)
                .orderBy(formattedDate.desc())
                .limit(1)
                .fetchOne();

        if(dates != null) {
            LocalDate date = DateUtils.stringToDate(dates);
            LocalDateTime startDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59);

            return queryFactory.selectFrom(presetMakersDailyFood)
                    .where(presetMakersDailyFood.createdDateTime.between(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate)), presetMakersDailyFood.deadline.after(now))
                    .fetch();
        }
        return null;
    }

//    public List<PresetMakersDailyFood> getMostRecentPresetsInDeadline() {
//        // 오늘을 구하고
//        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
//
//        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
//
//        LocalDate date = DateUtils.stringToDate(dates);
//        LocalDateTime startDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
//        LocalDateTime endDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59);
//
//        return queryFactory.selectFrom(presetMakersDailyFood)
//                .where(presetMakersDailyFood.createdDateTime.between(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate)), presetMakersDailyFood.deadline.after(now))
//                .fetch();
//
//        return null;
//    }

    public static StringTemplate getStringTemplate() {
        StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , presetMakersDailyFood.createdDateTime
                , ConstantImpl.create("%Y-%m-%d"));
        return formattedDate;
    }

}
