package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.DailyReport;
import co.dalicious.system.enums.DiningType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
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
}