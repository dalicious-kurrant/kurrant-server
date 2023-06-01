package co.dalicious.integration.client.user.reposiitory;

import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import co.dalicious.domain.user.entity.User;
import co.dalicious.integration.client.user.entity.MySpot;
import co.dalicious.integration.client.user.entity.MySpotZone;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.integration.client.user.entity.QMySpot.mySpot;

@Repository
@RequiredArgsConstructor
public class QMySpotRepository {

    private final JPAQueryFactory queryFactory;

    public List<MySpot> findMySpotByRequestedMySpotZones(List<RequestedMySpotZones> requestedMySpotZonesList) {
        return queryFactory.selectFrom(mySpot)
                .where(mySpot.requestedMySpotZones.in(requestedMySpotZonesList))
                .fetch();
    }

    public List<MySpot> findMySpotByMySpotZone(List<MySpotZone> mySpotZone) {
        return queryFactory.selectFrom(mySpot)
                .where(mySpot.mySpotZone.in(mySpotZone))
                .fetch();
    }

    public List<MySpot> findMySpotByUser(User user) {
        return queryFactory.selectFrom(mySpot)
                .where(mySpot.isActive.ne(false), mySpot.user.eq(user))
                .fetch();
    }
}
