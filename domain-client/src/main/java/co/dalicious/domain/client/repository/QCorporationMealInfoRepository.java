package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.QCorporationMealInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static co.dalicious.domain.client.entity.QCorporationMealInfo.corporationMealInfo;

@Repository
@RequiredArgsConstructor
public class QCorporationMealInfoRepository {

    private final JPAQueryFactory queryFactory;


    public CorporationMealInfo findOneById(BigInteger id) {
        return queryFactory.selectFrom(corporationMealInfo)
                .where(corporationMealInfo.id.eq(id))
                .fetchOne();
    }
}
