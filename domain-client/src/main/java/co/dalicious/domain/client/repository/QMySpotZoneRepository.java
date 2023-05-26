package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.Region;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.client.entity.QMySpotZone.mySpotZone;
import static co.dalicious.domain.client.entity.QRegion.region;

@Repository
@RequiredArgsConstructor
public class QMySpotZoneRepository {
    private final JPAQueryFactory queryFactory;

    public MySpotZone findExistMySpotZoneByZipcode(String zipcode) {
        Region region1 = queryFactory.selectFrom(region)
                .where(region.zipcodes.eq(zipcode))
                .fetchOne();

        if(region1 == null) return null;

        return queryFactory.selectFrom(mySpotZone)
                .where(mySpotZone.regionList.contains(region1))
                .fetchOne();
    }

    public List<String> findAllNameList() {
        return queryFactory.select(mySpotZone.name)
                .from(mySpotZone)
                .orderBy(mySpotZone.name.asc())
                .fetch();
    }

    public Page<MySpotZone> findAllMySpotZone(String name, String city, String county, List<String> villages, List<String> zipcodes, MySpotZoneStatus status, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(name != null) {
            whereCause.and(mySpotZone.name.eq(name));
        }
        if(city != null) {
            whereCause.and(mySpotZone.regionList.any().city.eq(city));
        }
        if(county != null) {
            whereCause.and(mySpotZone.regionList.any().country.eq(county));
        }
        if(villages != null && !villages.isEmpty()) {
            whereCause.and(mySpotZone.regionList.any().village.in(villages));
        }
        if(zipcodes != null && !zipcodes.isEmpty()) {
            whereCause.and(mySpotZone.regionList.any().zipcodes.in(zipcodes));
        }
        if(status != null) {
            whereCause.and(mySpotZone.mySpotZoneStatus.eq(status));
        }

        int offset = limit * (page - 1);

        QueryResults<MySpotZone> results = queryFactory.selectFrom(mySpotZone)
                .where(whereCause)
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }


}


