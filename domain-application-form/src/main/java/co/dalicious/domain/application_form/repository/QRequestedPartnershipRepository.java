package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedCorporation;
import co.dalicious.domain.application_form.entity.RequestedMakers;
import co.dalicious.domain.application_form.entity.RequestedPartnership;
import co.dalicious.domain.application_form.entity.enums.HomepageRequestedType;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static co.dalicious.domain.application_form.entity.QRequestedCorporation.requestedCorporation;
import static co.dalicious.domain.application_form.entity.QRequestedMakers.requestedMakers;
import static co.dalicious.domain.application_form.entity.QRequestedPartnership.requestedPartnership;

@Repository
@RequiredArgsConstructor
public class QRequestedPartnershipRepository {

    private final JPAQueryFactory queryFactory;

    public Page<RequestedCorporation> pageFindAllRequestedCorporation(Pageable pageable) {
        QueryResults<RequestedCorporation> results = queryFactory.selectFrom(requestedCorporation)
                .orderBy(requestedCorporation.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<RequestedMakers> pageFindAllRequestedMakers(Pageable pageable) {
        QueryResults<RequestedMakers> results = queryFactory.selectFrom(requestedMakers)
                .orderBy(requestedMakers.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
