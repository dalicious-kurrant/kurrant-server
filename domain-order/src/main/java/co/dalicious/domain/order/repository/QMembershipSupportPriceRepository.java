package co.dalicious.domain.order.repository;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.order.entity.MembershipSupportPrice;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.Membership;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.order.entity.QDailyFoodSupportPrice.dailyFoodSupportPrice;
import static co.dalicious.domain.order.entity.QMembershipSupportPrice.membershipSupportPrice;
import static co.dalicious.domain.order.entity.QOrder.order;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;
import static co.dalicious.domain.order.entity.QOrderItemMembership.orderItemMembership;
import static co.dalicious.domain.user.entity.QMembership.membership;

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

    public Map<BigInteger, Integer> getUserCountByGroupIdsAndPeriod(List<BigInteger> groupIds, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Tuple> results =  queryFactory.select(dailyFood.group.id, order.user.countDistinct())
                .from(orderItemDailyFood)
                .leftJoin(orderItemDailyFood.order, order)
                .leftJoin(orderItemDailyFood.dailyFood, dailyFood)
                .where(dailyFood.serviceDate.between(startDate, endDate),
                        orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()),
                        dailyFood.group.id.in(groupIds))
                .groupBy(dailyFood.group.id)
                .fetch();
        Map<BigInteger, Integer> resultMap = new HashMap<>();
        for (Tuple tuple : results) {
            resultMap.put(tuple.get(dailyFood.group.id), tuple.get(order.user.countDistinct()).intValue());
        }

        return resultMap;
    }

    public List<MembershipSupportPrice> findAllByGroupIdsAndPeriod(List<BigInteger> groupIds, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(23, 59, 59));

        BooleanBuilder whereClause = new BooleanBuilder();
        if(groupIds != null && !groupIds.isEmpty()) {
            whereClause.and(membershipSupportPrice.group.id.in(groupIds));
        }

        return queryFactory.selectFrom(membershipSupportPrice)
                .where(membershipSupportPrice.monetaryStatus.eq(MonetaryStatus.DEDUCTION),
                        whereClause,
                        membershipSupportPrice.createdDateTime.between(startTimestamp, endTimestamp))
                .fetch();
    }

    public List<MembershipSupportPrice> findAllByGroupAndPeriod(Corporation corporation, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(23, 59, 59));

        return queryFactory.selectFrom(membershipSupportPrice)
                .where(membershipSupportPrice.monetaryStatus.eq(MonetaryStatus.DEDUCTION),
                        membershipSupportPrice.group.id.eq(corporation.getId()),
                        membershipSupportPrice.createdDateTime.between(startTimestamp, endTimestamp))
                .fetch();
    }

    public List<Membership> findAllByGroupAndNow(Corporation corporation) {
        LocalDate now = LocalDate.now();

        return queryFactory.select(membership)
                .from(membershipSupportPrice)
                .leftJoin(membershipSupportPrice.orderItemMembership, orderItemMembership)
                .leftJoin(orderItemMembership.membership, membership)
                .where(membershipSupportPrice.group.id.eq(corporation.getId()),
                        membership.startDate.loe(now),
                        membership.endDate.goe(now))
                .fetch();
    }
}
