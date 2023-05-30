package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.dto.filter.FilterDto;
import co.dalicious.domain.client.dto.filter.FilterInfo;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.Region;
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
import java.util.BitSet;
import java.util.List;

import static co.dalicious.domain.client.entity.QMySpotZone.mySpotZone;
import static co.dalicious.domain.client.entity.QRegion.region;

@Repository
@RequiredArgsConstructor
public class QMySpotZoneRepository {
    private final JPAQueryFactory queryFactory;

    public MySpotZone findExistMySpotZoneByZipcode(String zipcode) {
        return queryFactory.selectFrom(mySpotZone)
                .where(mySpotZone.regionList.any().zipcode.eq(zipcode))
                .fetchOne();
    }

    public List<FilterInfo> findAllNameList() {
        List<Tuple> retsultList = queryFactory.select(mySpotZone.id, mySpotZone.name)
                .from(mySpotZone)
                .orderBy(mySpotZone.name.asc())
                .fetch();

        List<FilterInfo> filterInfos = new ArrayList<>();

        retsultList.forEach(result -> {
            FilterInfo filterInfo = new FilterInfo();

            filterInfo.setId(result.get(mySpotZone.id));
            filterInfo.setName(result.get(mySpotZone.name));
            filterInfos.add(filterInfo);
        });

        return filterInfos;
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
            whereCause.and(mySpotZone.regionList.any().city.eq(city));
        }
        if(county != null) {
            whereCause.and(mySpotZone.regionList.any().county.eq(county));
        }
        if(villages != null && !villages.isEmpty()) {
            whereCause.and(mySpotZone.regionList.any().village.in(villages));
        }
        if(zipcodes != null && !zipcodes.isEmpty()) {
            whereCause.and(mySpotZone.regionList.any().zipcode.in(zipcodes));
        }
        if(status != null) {
            whereCause.and(mySpotZone.mySpotZoneStatus.eq(status));
        }

        int offset = limit * (page - 1);

        QueryResults<MySpotZone> results = queryFactory.selectFrom(mySpotZone)
                .where(whereCause)
                .orderBy(mySpotZone.id.desc())
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public MySpotZone findExistMySpotZoneByZipcodes(List<String> zipcodes) {
        return queryFactory.selectFrom(mySpotZone)
                .where(mySpotZone.regionList.any().zipcode.in(zipcodes))
                .fetchOne();
    }

    public MySpotZone findMySpotZoneById(BigInteger id) {
        return queryFactory.selectFrom(mySpotZone)
                .where(mySpotZone.id.eq(id))
                .fetchOne();
    }
}


