package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Corporation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

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
}
