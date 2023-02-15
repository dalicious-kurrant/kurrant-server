package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static co.dalicious.domain.user.entity.QUserSpot.userSpot;

@Repository
@RequiredArgsConstructor
public class QUserSpotRepository {

    private final JPAQueryFactory queryFactory;

    public BigInteger findOneBySpotId(BigInteger spotId) {
        return queryFactory.select(userSpot.id)
                .from(userSpot)
                .where(userSpot.spot.id.eq(spotId))
                .fetchOne();
    }

    public User findOneById(BigInteger userSpotId) {
        User user = queryFactory.select(userSpot.user)
                .from(userSpot)
                .where(userSpot.id.eq(userSpotId))
                .fetchOne();

        if (user.getUserStatus().getCode() == 0){
            return null;
        }
        return user;
    }
}
