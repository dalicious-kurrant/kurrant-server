package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.system.enums.DiningType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static co.dalicious.domain.client.entity.QMealInfo.mealInfo;

@Repository
@RequiredArgsConstructor
public class QMealInfoRepository {
    private final JPAQueryFactory queryFactory;


    public MealInfo findBySpotId(BigInteger spotId, Integer diningTypeCode) {
        return queryFactory.selectFrom(mealInfo)
                .where(mealInfo.spot.id.eq(spotId),
                        mealInfo.diningType.eq(DiningType.ofCode(diningTypeCode)))
                .fetchOne();
    }
}
