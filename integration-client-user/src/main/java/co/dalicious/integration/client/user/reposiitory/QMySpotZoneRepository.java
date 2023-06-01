package co.dalicious.integration.client.user.reposiitory;

import co.dalicious.integration.client.user.entity.MySpotZone;
import co.dalicious.integration.client.user.dto.filter.FilterInfo;
import co.dalicious.integration.client.user.entity.enums.MySpotZoneStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static co.dalicious.domain.address.entity.QRegion.region;
import static co.dalicious.integration.client.user.entity.QMySpotZone.mySpotZone;

@Repository
@RequiredArgsConstructor
public class QMySpotZoneRepository {
    private final JPAQueryFactory queryFactory;

    public MySpotZone findExistMySpotZoneByZipcode(String zipcode) {
        return queryFactory.selectFrom(mySpotZone)
                .leftJoin(region).on(region.id.stringValue().contains(String.valueOf(mySpotZone.regionIds)))
                .where(region.zipcode.eq(zipcode), mySpotZone.isActive.ne(false))
                .fetchOne();
    }

    public List<String> findNameById(List<BigInteger> id) {
        return queryFactory.select(mySpotZone.name)
                .from(mySpotZone)
                .where(mySpotZone.id.in(id))
                .fetch();
    }


    public Page<MySpotZone> findAllMySpotZone(List<String> name, String city, String county, List<String> villages, List<String> zipcodes, MySpotZoneStatus status, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(name != null) {
            whereCause.and(mySpotZone.name.in(name));
        }
        if(city != null) {
            whereCause.and(region.city.eq(city));
        }
        if(county != null) {
            whereCause.and(region.county.eq(county));
        }
        if(villages != null && !villages.isEmpty()) {
            whereCause.and(region.village.in(villages));
        }
        if(zipcodes != null && !zipcodes.isEmpty()) {
            whereCause.and(region.zipcode.in(zipcodes));
        }
        if(status != null) {
            whereCause.and(mySpotZone.mySpotZoneStatus.eq(status));
        }

        int offset = limit * (page - 1);

        QueryResults<MySpotZone> results = queryFactory.selectFrom(mySpotZone)
                .leftJoin(region).on(region.id.stringValue().contains(String.valueOf(mySpotZone.regionIds)))
                .where(whereCause, mySpotZone.isActive.ne(false))
                .orderBy(mySpotZone.id.desc())
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public MySpotZone findExistMySpotZoneByZipcodes(List<String> zipcodes) {
        return queryFactory.selectFrom(mySpotZone)
                .leftJoin(region).on(region.id.stringValue().contains(String.valueOf(mySpotZone.regionIds)))
                .where(region.zipcode.in(zipcodes))
                .fetchOne();
    }

    public MySpotZone findMySpotZoneById(BigInteger id) {
        return queryFactory.selectFrom(mySpotZone)
                .where(mySpotZone.id.eq(id), mySpotZone.isActive.ne(false))
                .fetchOne();
    }

    public List<MySpotZone> findAllMySpotZoneByIds(List<BigInteger> ids) {
        return queryFactory.selectFrom(mySpotZone)
                .where(mySpotZone.id.in(ids), mySpotZone.isActive.ne(false))
                .fetch();
    }

    public List<MySpotZone> findAll() {
        return queryFactory.selectFrom(mySpotZone)
                .fetch();
    }
}


