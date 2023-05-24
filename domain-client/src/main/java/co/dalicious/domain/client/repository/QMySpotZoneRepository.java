package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.MySpotZone;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static co.dalicious.domain.client.entity.QMySpotZone.mySpotZone;

@Repository
@RequiredArgsConstructor
public class QMySpotZoneRepository {
    private final JPAQueryFactory queryFactory;

    public MySpotZone findExistMySpotZoneByZipcode(String zipcode) {
        return queryFactory.selectFrom(mySpotZone)
                .where(mySpotZone.zipcodes.contains(zipcode))
                .fetchOne();
    }
}
