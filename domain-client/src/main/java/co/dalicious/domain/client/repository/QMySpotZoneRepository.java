package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.dto.FilterInfo;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.QMySpotZone;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
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

import static co.dalicious.integration.client.user.entity.QRegion.region;

@Repository
@RequiredArgsConstructor
public class QMySpotZoneRepository {
    private final JPAQueryFactory queryFactory;

    public MySpotZone findExistMySpotZoneByZipcode(String zipcode) {
        return queryFactory.selectFrom(QMySpotZone.mySpotZone)
                .leftJoin(region).on(region.mySpotZoneIds.eq(QMySpotZone.mySpotZone.id))
                .where(region.zipcode.eq(zipcode), QMySpotZone.mySpotZone.isActive.ne(false))
                .fetchOne();
    }

    public List<FilterInfo> findAllNameList() {
        List<Tuple> retsultList = queryFactory.select(QMySpotZone.mySpotZone.id, QMySpotZone.mySpotZone.name)
                .from(QMySpotZone.mySpotZone)
                .where(QMySpotZone.mySpotZone.isActive.ne(false))
                .orderBy(QMySpotZone.mySpotZone.name.asc())
                .fetch();

        List<FilterInfo> filterInfos = new ArrayList<>();

        retsultList.forEach(result -> {
            FilterInfo filterInfo = new FilterInfo();

            filterInfo.setId(result.get(QMySpotZone.mySpotZone.id));
            filterInfo.setName(result.get(QMySpotZone.mySpotZone.name));
            filterInfos.add(filterInfo);
        });

        return filterInfos;
    }

    public List<String> findNameById(List<BigInteger> id) {
        return queryFactory.select(QMySpotZone.mySpotZone.name)
                .from(QMySpotZone.mySpotZone)
                .where(QMySpotZone.mySpotZone.id.in(id))
                .fetch();
    }


    public Page<MySpotZone> findAllMySpotZone(List<String> name, String city, String county, List<String> villages, List<String> zipcodes, MySpotZoneStatus status, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(name != null) {
            whereCause.and(QMySpotZone.mySpotZone.name.in(name));
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
            whereCause.and(QMySpotZone.mySpotZone.mySpotZoneStatus.eq(status));
        }

        int offset = limit * (page - 1);

        QueryResults<MySpotZone> results = queryFactory.selectFrom(QMySpotZone.mySpotZone)
                .leftJoin(region).on(region.mySpotZoneIds.eq(QMySpotZone.mySpotZone.id))
                .where(whereCause, QMySpotZone.mySpotZone.isActive.ne(false))
                .orderBy(QMySpotZone.mySpotZone.id.desc())
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public MySpotZone findExistMySpotZoneByZipcodes(List<String> zipcodes) {
        return queryFactory.selectFrom(QMySpotZone.mySpotZone)
                .leftJoin(region).on(region.mySpotZoneIds.eq(QMySpotZone.mySpotZone.id))
                .where(region.zipcode.in(zipcodes))
                .fetchOne();
    }

    public MySpotZone findMySpotZoneById(BigInteger id) {
        return queryFactory.selectFrom(QMySpotZone.mySpotZone)
                .where(QMySpotZone.mySpotZone.id.eq(id), QMySpotZone.mySpotZone.isActive.ne(false))
                .fetchOne();
    }

    public List<MySpotZone> findAllMySpotZoneByIds(List<BigInteger> ids) {
        return queryFactory.selectFrom(QMySpotZone.mySpotZone)
                .where(QMySpotZone.mySpotZone.id.in(ids), QMySpotZone.mySpotZone.isActive.ne(false))
                .fetch();
    }

    public List<MySpotZone> findExistMySpotZoneListByZipcodes(List<String> zipcodes) {
        return queryFactory.selectFrom(QMySpotZone.mySpotZone)
                .leftJoin(region).on(region.mySpotZoneIds.eq(QMySpotZone.mySpotZone.id))
                .where(region.zipcode.in(zipcodes), QMySpotZone.mySpotZone.isActive.ne(false))
                .fetch();
    }
}


