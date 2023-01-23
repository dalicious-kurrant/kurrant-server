package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.UserSupportPriceHistory;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.order.entity.QUserSupportPriceHistory.userSupportPriceHistory;

@Repository
@RequiredArgsConstructor
public class QUserSupportPriceHistoryRepository {
    public final JPAQueryFactory queryFactory;

    public List<UserSupportPriceHistory> findAllUserSupportPriceHistoryBetweenServiceDate(User user, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(userSupportPriceHistory)
                .where(userSupportPriceHistory.user.eq(user),
                        userSupportPriceHistory.serviceDate.between(startDate,endDate))
                .fetch();
    }
}
