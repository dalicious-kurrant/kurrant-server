package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PointStatus;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static co.dalicious.domain.user.entity.QPointHistory.pointHistory;

@Repository
@RequiredArgsConstructor
public class QPointHistoryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<PointHistory> findAllByPointPolicy(PointPolicy policy) {
        return jpaQueryFactory.selectFrom(pointHistory)
                .where(pointHistory.pointPolicyId.eq(policy.getId()), pointHistory.point.eq(BigDecimal.valueOf(0)))
                .fetch();
    }

    public List<PointHistory> findAllByUserAndPointPolicy(User user, PointPolicy pointPolicy) {
        return jpaQueryFactory.selectFrom(pointHistory)
                .where(pointHistory.user.eq(user), pointHistory.pointPolicyId.eq(pointPolicy.getId()))
                .fetch();
    }

    public Page<PointHistory> findAllPointHistory(User user, Pageable pageable) {

        QueryResults<PointHistory> results =  jpaQueryFactory.selectFrom(pointHistory)
                .where(pointHistory.user.eq(user), pointHistory.point.ne(BigDecimal.ZERO))
                .orderBy(pointHistory.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<PointHistory> findAllPointHistoryByRewardStatus(User user, Pageable pageable) {

        QueryResults<PointHistory> results =  jpaQueryFactory.selectFrom(pointHistory)
                .where(pointHistory.user.eq(user), pointHistory.point.ne(BigDecimal.ZERO), pointHistory.pointStatus.in(PointStatus.rewardStatus()))
                .orderBy(pointHistory.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<PointHistory> findAllPointHistoryByUseStatus(User user, Pageable pageable) {

        QueryResults<PointHistory> results =  jpaQueryFactory.selectFrom(pointHistory)
                .where(pointHistory.user.eq(user), pointHistory.point.ne(BigDecimal.ZERO), pointHistory.pointStatus.eq(PointStatus.USED))
                .orderBy(pointHistory.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
