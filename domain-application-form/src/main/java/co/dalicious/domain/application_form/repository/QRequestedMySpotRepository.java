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
                .limit(1)      //to.지성 -> 결과가 2개 일 경우 에러가 나서 수정했습니다.
                .fetchOne();
    }

    public RequestedMySpot findRequestedMySpotById (BigInteger id) {
        return queryFactory.selectFrom(requestedMySpot)
                .where(requestedMySpot.id.eq(id))
                .fetchOne();
    }
}
