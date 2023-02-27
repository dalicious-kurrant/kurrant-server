package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.CorporationMealInfo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.client.entity.QCorporation.corporation;

@Repository
@RequiredArgsConstructor
public class QCorporationRepository {

    private final JPAQueryFactory queryFactory;


    public BigInteger findOneByCode(String code) {
        return queryFactory.select(corporation.id)
                .from(corporation)
                .where(corporation.code.eq(code))
                .fetchOne();
    }

    public Corporation findEntityByCode(String code) {
        return queryFactory.selectFrom(corporation)
                .where(corporation.code.eq(code))
                .fetchOne();
    }

    public Page<Corporation> findAll(BigInteger groupId, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if(groupId != null) {
            whereClause.and(corporation.id.eq(groupId));
        }

        int itemLimit = limit * page;
        int offset = itemLimit * (page - 1);

        QueryResults<Corporation> results = queryFactory.selectFrom(corporation)
                .where(whereClause)
                .limit(itemLimit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());

    }

}
