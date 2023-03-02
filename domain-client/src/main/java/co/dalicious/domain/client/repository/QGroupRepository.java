package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Group;
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
import java.util.Set;

import static co.dalicious.domain.client.entity.QGroup.group;

@Repository
@RequiredArgsConstructor
public class QGroupRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Group> findAll(BigInteger groupId, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if(groupId != null) {
            whereClause.and(group.id.eq(groupId));
        }

        int offset = limit * (page - 1);

        QueryResults<Group> results = queryFactory.selectFrom(group)
                .where(whereClause)
                .orderBy(group.id.asc())
                .limit(limit)
                .offset(offset)
                .fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public List<Group> findAllByNames(Set<String> groupNames) {
        return queryFactory.selectFrom(group)
                .where(group.name.in(groupNames))
                .fetch();
    }

    public List<Group> findAllByIds(Set<BigInteger> ids) {
        return queryFactory.selectFrom(group)
                .where(group.id.in(ids))
                .fetch();
    }

}
