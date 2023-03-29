package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.entity.enums.PointCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QPointPolicyRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<PointPolicy> findAllPointPolicyByCondition(List<PointCondition> pointConditionList) {
        return jpaQueryFactory.selectFrom(pointPolicy)
                .where(pointPolicy.pointCondition.in(pointConditionList))
                .fetch();
    }
}
