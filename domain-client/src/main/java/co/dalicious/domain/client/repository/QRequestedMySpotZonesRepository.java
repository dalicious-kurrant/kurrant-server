package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.RequestedMySpotZones;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.client.entity.QRequestedMySpotZones.requestedMySpotZones;

@Repository
@RequiredArgsConstructor
public class QRequestedMySpotZonesRepository {

    private final JPAQueryFactory queryFactory;

    public List<String> findAllCity() {
        return queryFactory.select(requestedMySpotZones.city)
                .from(requestedMySpotZones)
                .groupBy(requestedMySpotZones.city)
                .orderBy(requestedMySpotZones.city.asc())
                .fetch();
    }

    public List<String> findAllCountyByCity(String city) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(requestedMySpotZones.city.eq(city));
        }

        return queryFactory.select(requestedMySpotZones.county).from(requestedMySpotZones)
                .where(whereCause)
                .groupBy(requestedMySpotZones.county)
                .orderBy(requestedMySpotZones.county.asc())
                .fetch();
    }

    public List<String> findAllVillageByCounty(String city, String county) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(requestedMySpotZones.city.eq(city));
        }

        if (county != null) {
            whereCause.and(requestedMySpotZones.county.eq(county));
        }

        return queryFactory.select(requestedMySpotZones.village)
                .from(requestedMySpotZones)
                .where(whereCause)
                .fetch();
    }

    public List<String> findAllZipcodeByCityAndCountyAndVillage(String city, String county, List<String> village) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(requestedMySpotZones.city.eq(city));
        }
        if (county != null) {
            whereCause.and(requestedMySpotZones.county.eq(county));
        }
        if (village != null && !village.isEmpty()) {
            whereCause.and(requestedMySpotZones.village.in(village));
        }

        return queryFactory.select(requestedMySpotZones.zipcode)
                .from(requestedMySpotZones)
                .where(whereCause)
                .fetch();
    }

    public Page<RequestedMySpotZones> findAllRequestedMySpotZonesByFilter(String city, String county, List<String> villages, List<String> zipcodes,
                                                                          Integer min, Integer max, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(requestedMySpotZones.city.eq(city));
        }
        if (county != null) {
            whereCause.and(requestedMySpotZones.county.eq(county));
        }
        if (villages != null && !villages.isEmpty()) {
            whereCause.and(requestedMySpotZones.village.in(villages));
        }
        if (zipcodes != null && !zipcodes.isEmpty()) {
            whereCause.and(requestedMySpotZones.zipcode.in(zipcodes));
        }
        if (min != null) {
            whereCause.and(requestedMySpotZones.waitingUserCount.goe(min));
        }
        if (max != null) {
            whereCause.and(requestedMySpotZones.waitingUserCount.loe(max));
        }

        int offset = limit * (page - 1);

        QueryResults<RequestedMySpotZones> results = queryFactory.selectFrom(requestedMySpotZones)
                .where(whereCause)
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public RequestedMySpotZones findRequestedMySpotZoneByZipcode(String zipcode) {
        return queryFactory.selectFrom(requestedMySpotZones)
                .where(requestedMySpotZones.zipcode.eq(zipcode))
                .fetchOne();
    }

    public List<RequestedMySpotZones> findRequestedMySpotZonesByIds(List<BigInteger> ids) {
        return queryFactory.selectFrom(requestedMySpotZones)
                .where(requestedMySpotZones.id.in(ids))
                .fetch();
    }

}
