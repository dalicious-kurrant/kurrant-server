package co.dalicious.domain.paycheck.repository;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.CorporationPaycheck;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Coalesce;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static co.dalicious.domain.paycheck.entity.QCorporationPaycheck.corporationPaycheck;

@Repository
@RequiredArgsConstructor
public class QCorporationPaycheckRepository {
    private final JPAQueryFactory queryFactory;

    public List<CorporationPaycheck> getCorporationPaychecksByFilter(YearMonth startYearMonth, YearMonth endYearMonth, List<BigInteger> corporationIds, PaycheckStatus paycheckStatus, Boolean hasRequest) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if (startYearMonth != null) {
            whereClause.and(corporationPaycheck.yearMonth.goe(startYearMonth));
        }
        if (endYearMonth != null) {
            whereClause.and(corporationPaycheck.yearMonth.loe(endYearMonth));
        }
        if (corporationIds != null && !corporationIds.isEmpty()) {
            whereClause.and(corporationPaycheck.corporation.id.in(corporationIds));
        }
        if (paycheckStatus != null) {
            whereClause.and(corporationPaycheck.paycheckStatus.eq(paycheckStatus));
        }
        if (hasRequest != null) {
            whereClause.and(hasRequest ? corporationPaycheck.paycheckMemos.isNotEmpty() : corporationPaycheck.paycheckMemos.isEmpty());
        }
        return queryFactory.selectFrom(corporationPaycheck)
                .where(whereClause)
                .orderBy(corporationPaycheck.createdDateTime.asc())
                .fetch();
    }

    public List<CorporationPaycheck> getCorporationPaychecksByFilter(Corporation corporation, YearMonth startYearMonth, YearMonth endYearMonth, PaycheckStatus paycheckStatus, Boolean hasRequest) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(whereClause.and(corporationPaycheck.corporation.eq(corporation)));

        if (startYearMonth != null) {
            whereClause.and(corporationPaycheck.yearMonth.goe(startYearMonth));
        }
        if (endYearMonth != null) {
            whereClause.and(corporationPaycheck.yearMonth.loe(endYearMonth));
        }
        if (paycheckStatus != null) {
            whereClause.and(corporationPaycheck.paycheckStatus.eq(paycheckStatus));
        }
        if (hasRequest != null) {
            whereClause.and(hasRequest ? corporationPaycheck.paycheckMemos.isNotEmpty() : corporationPaycheck.paycheckMemos.isEmpty());
        }
        return queryFactory.selectFrom(corporationPaycheck)
                .where(whereClause)
                .orderBy(corporationPaycheck.createdDateTime.asc())
                .fetch();
    }

    public List<CorporationPaycheck> getCorporationPaychecksByFilter(List<BigInteger> groupIds, YearMonth yearMonth) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if (groupIds != null && !groupIds.isEmpty()) {
            whereClause.and(corporationPaycheck.corporation.id.in(groupIds));
        }
        return queryFactory.selectFrom(corporationPaycheck)
                .where(whereClause, corporationPaycheck.yearMonth.eq(yearMonth))
                .fetch();
    }
}
