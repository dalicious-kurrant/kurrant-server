package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import com.querydsl.core.BooleanBuilder;
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


    public long deleteSpots(List<BigInteger> spotIdList) {
        return queryFactory.update(spot)
                .set(spot.status , SpotStatus.INACTIVE)
                .where(spot.id.in(spotIdList))
                .execute();
    }

    public List<Spot> findAllByStatus(Integer status) {
        if(status == null) {
            return queryFactory.selectFrom(spot)
                    .fetch();
        }
        else if (status == 1) {
            return queryFactory.selectFrom(spot)
                    .where(spot.status.eq(SpotStatus.ACTIVE))
                    .fetch();
        }
        else if(status == 0) {
            return queryFactory.selectFrom(spot)
                    .where(spot.status.eq(SpotStatus.INACTIVE))
                    .fetch();
        }
        return null;
    }

    public List<Spot> findAllByIds(List<BigInteger> ids) {
        return queryFactory.selectFrom(spot)
                .where(spot.id.in(ids))
                .fetch();
    }
}
