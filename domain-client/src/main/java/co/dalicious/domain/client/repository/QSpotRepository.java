package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.client.entity.QSpot.spot;

@Repository
@RequiredArgsConstructor
public class QSpotRepository {

    private final JPAQueryFactory queryFactory;


    public List<BigInteger> findAllByGroupId(BigInteger corporationId) {
        return queryFactory
                .select(spot.id)
                .from(spot)
                .where(spot.group.id.eq(corporationId))
                .fetch();
    }

    public void testSJ() {
        //DateFormat 테스트 OK
         StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0},{1})"
                ,spot.createdDateTime
                , ConstantImpl.create("%Y-%m-%d"));


         queryFactory
                .select(formattedDate)
                .from(spot)
                .groupBy(formattedDate)
                .orderBy(spot.createdDateTime.desc())
                .limit(2);
    }

    public long deleteSpot(BigInteger spotId) {
        return queryFactory.update(spot)
                .set(spot.status , SpotStatus.INACTIVE)
                .where(spot.id.eq(spotId))
                .execute();
    }

    public List<Spot> findAll() {
        return queryFactory.selectFrom(spot)
                .where(spot.status.eq(SpotStatus.ACTIVE))
                .fetch();
    }
}
