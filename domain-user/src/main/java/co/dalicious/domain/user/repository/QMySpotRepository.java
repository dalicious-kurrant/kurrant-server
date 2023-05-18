package co.dalicious.domain.user.repository;

import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import co.dalicious.domain.user.entity.MySpot;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.user.entity.QMySpot.mySpot;

@Repository
@RequiredArgsConstructor
public class QMySpotRepository {

    private final JPAQueryFactory queryFactory;

    public List<MySpot> findMySpotByRequestedMySpotZones(List<RequestedMySpotZones> requestedMySpotZonesList) {
        return queryFactory.selectFrom(mySpot)
                .where(mySpot.requestedMySpotZones.in(requestedMySpotZonesList))
                .fetch();
    }
}
