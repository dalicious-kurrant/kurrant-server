package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import co.dalicious.domain.application_form.entity.RequestedShareSpot;
import co.dalicious.domain.application_form.entity.enums.ShareSpotRequestType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.application_form.entity.QRequestedMySpotZones.requestedMySpotZones;
import static co.dalicious.domain.application_form.entity.QRequestedShareSpot.requestedShareSpot;

@Repository
@RequiredArgsConstructor
public class QRequestedShareSpotRepository {
    private final JPAQueryFactory queryFactory;

    public Page<RequestedShareSpot> findAllByType(Integer type, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if(type != null) {
            ShareSpotRequestType shareSpotRequestType = ShareSpotRequestType.ofCode(type);
            whereClause.and(requestedShareSpot.shareSpotRequestType.eq(shareSpotRequestType));
        }
        int offset = limit * (page - 1);

        QueryResults<RequestedShareSpot> results = queryFactory.selectFrom(requestedShareSpot)
                .where(whereClause)
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}