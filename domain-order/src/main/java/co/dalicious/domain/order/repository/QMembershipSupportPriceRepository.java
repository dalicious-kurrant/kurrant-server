package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.MembershipSupportPrice;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.system.util.DateUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static co.dalicious.domain.order.entity.QMembershipSupportPrice.membershipSupportPrice;

@Repository
@RequiredArgsConstructor
public class QMembershipSupportPriceRepository {
    private final JPAQueryFactory queryFactory;

    public List<MembershipSupportPrice> findAllByPeriod(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(23, 59, 59));

        return queryFactory.selectFrom(membershipSupportPrice)
                .where(membershipSupportPrice.monetaryStatus.eq(MonetaryStatus.DEDUCTION),
                        membershipSupportPrice.createdDateTime.between(startTimestamp, endTimestamp))
                .fetch();
    }
}
