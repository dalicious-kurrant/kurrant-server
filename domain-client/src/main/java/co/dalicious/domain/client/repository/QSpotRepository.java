package co.dalicious.domain.client.repository;

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
}
