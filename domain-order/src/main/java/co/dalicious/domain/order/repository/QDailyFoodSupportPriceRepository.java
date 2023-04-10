package co.dalicious.domain.order.repository;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.order.entity.QDailyFoodSupportPrice.dailyFoodSupportPrice;

@Repository
@RequiredArgsConstructor
public class QDailyFoodSupportPriceRepository {
    public final JPAQueryFactory queryFactory;

    public List<DailyFoodSupportPrice> findAllUserSupportPriceHistoryBetweenServiceDate(User user, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(dailyFoodSupportPrice)
                .where(dailyFoodSupportPrice.user.eq(user),
                        dailyFoodSupportPrice.serviceDate.between(startDate,endDate))
                .fetch();
    }

    public List<DailyFoodSupportPrice> findAllUserSupportPriceHistoryBySpotBetweenServiceDate(User user, Group group, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(dailyFoodSupportPrice)
                .where(dailyFoodSupportPrice.user.eq(user),
                        dailyFoodSupportPrice.group.eq(group),
                        dailyFoodSupportPrice.serviceDate.between(startDate,endDate))
                .fetch();
    }
}
