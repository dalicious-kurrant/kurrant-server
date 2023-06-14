package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.enums.SpotStatus;
import co.dalicious.domain.client.entity.MySpot;
import co.dalicious.domain.client.entity.MySpotZone;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class QMySpotRepository {

    private final JPAQueryFactory queryFactory;

//    public List<MySpot> findMySpotByUserIds(List<BigInteger> userIds) {
//        return queryFactory.selectFrom(QMySpot.mySpot)
//                .where(QMySpot.mySpot.userId.in(userIds))
//                .fetch();
//    }
//
//    public List<MySpot> findMySpotByMySpotZone(List<MySpotZone> mySpotZone) {
//        return queryFactory.selectFrom(QMySpot.mySpot)
//                .where(QMySpot.mySpot.group.in(mySpotZone))
//                .fetch();
//    }
//
//    public List<MySpot> findMySpotByUser(User user) {
//        return queryFactory.selectFrom(QMySpot.mySpot)
//                .where(QMySpot.mySpot.status.eq(SpotStatus.ACTIVE), QMySpot.mySpot.userId.eq(user.getId()))
//                .fetch();
//    }
}
