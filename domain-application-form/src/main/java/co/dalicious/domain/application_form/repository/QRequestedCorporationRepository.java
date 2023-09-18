package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedCorporation;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static co.dalicious.domain.application_form.entity.QRequestedCorporation.requestedCorporation;

@Repository
@RequiredArgsConstructor
public class QRequestedCorporationRepository {

    private final JPAQueryFactory queryFactory;

    public Page<RequestedCorporation> pageFindAllRequestedCorporation(Pageable pageable) {
        QueryResults<RequestedCorporation> results = queryFactory.selectFrom(requestedCorporation)
                .orderBy(requestedCorporation.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
