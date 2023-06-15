package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedMySpot;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static co.dalicious.domain.application_form.entity.QRequestedMySpot.requestedMySpot;

@Repository
@RequiredArgsConstructor
public class QRequestedMySpotRepository {
    private final JPAQueryFactory queryFactory;

    public RequestedMySpot findRequestedMySpotByUserId (BigInteger userId) {
        return queryFactory.selectFrom(requestedMySpot)
                .where(requestedMySpot.userId.eq(userId))
                .fetchOne();
    }

    public RequestedMySpot findRequestedMySpotById (BigInteger id) {
        return queryFactory.selectFrom(requestedMySpot)
                .where(requestedMySpot.id.eq(id))
                .fetchOne();
    }
}
