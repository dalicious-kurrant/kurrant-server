package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static co.dalicious.domain.user.entity.QPointHistory.pointHistory;

@Repository
@RequiredArgsConstructor
public class QPointHistoryRepository {

    private JPAQueryFactory jpaQueryFactory;

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
}
