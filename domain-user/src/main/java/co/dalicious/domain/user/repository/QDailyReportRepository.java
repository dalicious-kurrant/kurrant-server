package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.DailyReport;
import co.dalicious.system.enums.DiningType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;

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

        if (result != null){
            throw new ApiException(ExceptionEnum.DUPLICATED_DINING_TYPE);
        }

    }
}
