package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.dto.DailyReportByDate;
import co.dalicious.domain.user.entity.DailyReport;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.user.entity.QDailyReport.dailyReport;

@Repository
@RequiredArgsConstructor
public class QDailyReportRepository {

    private final JPAQueryFactory queryFactory;


    public void findByUserIdAndDiningType(BigInteger userId, Integer diningType, String eatDate) {

        DailyReport result = queryFactory.selectFrom(dailyReport)
                .where(dailyReport.user.id.eq(userId),
                        dailyReport.diningType.eq(DiningType.ofCode(diningType)),
                        dailyReport.eatDate.eq(LocalDate.parse(eatDate)))
                .fetchOne();


    }

    public List<DailyReport> findByUserIdAndDate(BigInteger userId, String stringDate) {

        LocalDate date = LocalDate.parse(stringDate);

        return queryFactory.selectFrom(dailyReport)
                .where(dailyReport.user.id.eq(userId),
                        dailyReport.eatDate.eq(date))
                .fetch();

    }

    public void saveDailyReportFood(BigInteger userId, LocalDate startDate, LocalDate endDate) {



    }

    public long deleteReport(BigInteger userId, BigInteger reportId) {
        return queryFactory.delete(dailyReport)
                .where(dailyReport.user.id.eq(userId),
                        dailyReport.id.eq(reportId))
                .execute();
    }

    public List<DailyReportByDate> findByUserIdAndDateBetween(BigInteger userId, LocalDate startDate, LocalDate endDate) {

        return queryFactory.select(Projections.constructor(DailyReportByDate.class,
                        dailyReport.eatDate.as("eatDate"),
                        dailyReport.calorie.sum().as("calorie"),
                        dailyReport.carbohydrate.sum().as("carbohydrate"),
                        dailyReport.protein.sum().as("protein"),
                        dailyReport.fat.sum().as("fat")))
                .from(dailyReport)
                .groupBy(dailyReport.eatDate, dailyReport.user.id)
                .having(dailyReport.user.id.eq(userId),
                        dailyReport.eatDate.between(startDate, endDate))
                .orderBy(dailyReport.eatDate.asc())
                .fetch();
    }

    public List<DailyReport> findAllByUserId(BigInteger userId) {
        return queryFactory.selectFrom(dailyReport)
                .where(dailyReport.user.id.eq(userId))
                .fetch();
    }
}
