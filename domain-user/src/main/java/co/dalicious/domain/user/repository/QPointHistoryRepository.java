package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.entity.User;
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

    public Page<PointHistory> findAllPointHistory(User user, Integer limit, Integer page, Pageable pageable) {
        int offset = limit - (page -1);

        QueryResults<PointHistory> results =  jpaQueryFactory.selectFrom(pointHistory)
                .where(pointHistory.user.eq(user), pointHistory.point.ne(BigDecimal.valueOf(0)))
                .orderBy(pointHistory.createdDateTime.desc())
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
