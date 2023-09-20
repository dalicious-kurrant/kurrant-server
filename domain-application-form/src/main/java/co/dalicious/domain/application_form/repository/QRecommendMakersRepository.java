package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RecommendMakers;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.application_form.entity.QRecommendMakers.recommendMakers;

@Repository
@RequiredArgsConstructor
public class QRecommendMakersRepository {
    private final JPAQueryFactory queryFactory;

    public RecommendMakers findBySpotId(List<BigInteger> ids, BigInteger spotId) {
        return queryFactory.selectFrom(recommendMakers)
                .where(recommendMakers.id.in(ids), recommendMakers.groupId.eq(spotId))
                .fetchOne();
    }
}
