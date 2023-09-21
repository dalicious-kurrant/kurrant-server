package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.dto.makers.RecommendMakersDto;
import co.dalicious.domain.application_form.entity.RecommendMakers;
import co.dalicious.domain.application_form.entity.enums.RecommendProgressStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    public Page<RecommendMakers> findAllByFilter(RecommendProgressStatus status, BigInteger makersName, BigInteger groupId, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if (status != null) {
            whereCause.and(recommendMakers.progressStatus.eq(status));
        }
        if (makersName != null) {
            String name = queryFactory.select(recommendMakers.name).from(recommendMakers).where(recommendMakers.id.eq(makersName)).fetchOne();
            whereCause.and(recommendMakers.name.eq(name));
        }
        if (groupId != null) {
            whereCause.and(recommendMakers.groupId.eq(groupId));
        }

        QueryResults<RecommendMakers> results = queryFactory.selectFrom(recommendMakers)
                .where(whereCause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public List<RecommendMakersDto> findAllIdAndMakersName() {
        return queryFactory.select(Projections.fields(RecommendMakersDto.class, recommendMakers.id, recommendMakers.name))
                .from(recommendMakers)
                .groupBy(recommendMakers.name)
                .fetch();
    }
}
