package co.dalicious.domain.paycheck.repository;

import co.dalicious.domain.paycheck.entity.CorporationPaycheck;
import co.dalicious.domain.paycheck.entity.ExpectedPaycheck;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.paycheck.entity.QExpectedPaycheck.expectedPaycheck;

@Repository
@RequiredArgsConstructor
public class QExpectedPaycheckRepository {
    private final JPAQueryFactory queryFactory;

    public List<ExpectedPaycheck> findAllByCorporationPaychecks(List<CorporationPaycheck> corporationPaychecks) {
        return queryFactory.selectFrom(expectedPaycheck)
                .where(expectedPaycheck.corporationPaycheck.in(corporationPaychecks))
                .fetch();
    }

}
