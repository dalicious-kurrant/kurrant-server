package co.dalicious.domain.payment.repository;

import co.dalicious.domain.payment.entity.PointPolicy;
import co.dalicious.domain.payment.entity.enums.PointCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.payment.entity.QPointPolicy.pointPolicy;

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
