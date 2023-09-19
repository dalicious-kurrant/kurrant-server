package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedPartnership;
import co.dalicious.domain.application_form.entity.enums.HomepageRequestedType;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static co.dalicious.domain.application_form.entity.QRequestedPartnership.requestedPartnership;

@Repository
@RequiredArgsConstructor
public class QRequestedPartnershipRepository {

    private final JPAQueryFactory queryFactory;

    public Page<RequestedPartnership> pageFindAllRequestedCorporation(Pageable pageable) {
        QueryResults<RequestedPartnership> results = queryFactory.selectFrom(requestedPartnership)
                .where(requestedPartnership.requestedType.eq(HomepageRequestedType.CORPORATION))
                .orderBy(requestedPartnership.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<RequestedPartnership> pageFindAllRequestedMakers(Pageable pageable) {
        QueryResults<RequestedPartnership> results = queryFactory.selectFrom(requestedPartnership)
                .where(requestedPartnership.requestedType.eq(HomepageRequestedType.MAKERS))
                .orderBy(requestedPartnership.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
