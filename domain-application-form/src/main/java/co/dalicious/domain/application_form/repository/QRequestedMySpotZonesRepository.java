package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterInfo;
import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
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
import static co.dalicious.domain.application_form.entity.QRequestedMySpotZones.requestedMySpotZones;


@Repository
@RequiredArgsConstructor
public class QRequestedMySpotZonesRepository {

    private final JPAQueryFactory queryFactory;

    public List<FilterInfo> findAllCity() {

        List<Tuple> resultList = queryFactory.select(requestedMySpotZones.id, requestedMySpotZones.region.city)
                .from(requestedMySpotZones)
                .groupBy(requestedMySpotZones.region.city)
                .orderBy(requestedMySpotZones.region.city.asc())
                .fetch();

        List<FilterInfo> filterInfos = new ArrayList<>();

        if(resultList.isEmpty()) return filterInfos;

        resultList.forEach(result -> {
            FilterInfo filterInfo = new FilterInfo();

            filterInfo.setId(result.get(requestedMySpotZones.id));
            filterInfo.setName(result.get(requestedMySpotZones.region.city));

            filterInfos.add(filterInfo);
        });

        return filterInfos;
    }

    public List<FilterInfo> findAllCountyByCity(String city) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(requestedMySpotZones.region.city.eq(city));
        }

        List<Tuple> resultList = queryFactory.select(requestedMySpotZones.id, requestedMySpotZones.region.county)
                .from(requestedMySpotZones)
                .where(whereCause)
                .groupBy(requestedMySpotZones.region.county)
                .orderBy(requestedMySpotZones.region.county.asc())
                .fetch();

        List<FilterInfo> filterInfos = new ArrayList<>();

        if(resultList.isEmpty()) return filterInfos;

        resultList.forEach(result -> {
            FilterInfo filterInfo = new FilterInfo();

            filterInfo.setId(result.get(requestedMySpotZones.id));
            filterInfo.setName(result.get(requestedMySpotZones.region.county));

            filterInfos.add(filterInfo);
        });

        return filterInfos;
    }

    public List<FilterInfo> findAllVillageByCounty(String city, String county) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(requestedMySpotZones.region.city.eq(city));
        }

        if (county != null) {
            whereCause.and(requestedMySpotZones.region.county.eq(county));
        }

        List<Tuple> resultList = queryFactory.select(requestedMySpotZones.id, requestedMySpotZones.region.village)
                .from(requestedMySpotZones)
                .where(whereCause)
                .groupBy(requestedMySpotZones.region)
                .fetch();

        List<FilterInfo> filterInfos = new ArrayList<>();

        if(resultList.isEmpty()) return filterInfos;

        resultList.forEach(result -> {
            FilterInfo filterInfo = new FilterInfo();

            filterInfo.setId(result.get(requestedMySpotZones.id));
            filterInfo.setName(result.get(requestedMySpotZones.region.village));

            filterInfos.add(filterInfo);
        });

        return filterInfos;
    }

    public List<FilterInfo> findAllZipcodeByCityAndCountyAndVillage(String city, String county, List<String> village) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(requestedMySpotZones.region.city.eq(city));
        }
        if (county != null) {
            whereCause.and(requestedMySpotZones.region.county.eq(county));
        }
        if (village != null && !village.isEmpty()) {
            whereCause.and(requestedMySpotZones.region.village.in(village));
        }

        List<Tuple> resultList =  queryFactory.select(requestedMySpotZones.id, requestedMySpotZones.region.zipcode)
                .from(requestedMySpotZones)
                .where(whereCause)
                .fetch();

        List<FilterInfo> filterInfos = new ArrayList<>();

        if(resultList.isEmpty()) return filterInfos;

        resultList.forEach(result -> {
            FilterInfo filterInfo = new FilterInfo();

            filterInfo.setId(result.get(requestedMySpotZones.id));
            filterInfo.setName(result.get(requestedMySpotZones.region.zipcode));

            filterInfos.add(filterInfo);
        });

        return filterInfos;
    }

    public Page<RequestedMySpotZones> findAllRequestedMySpotZonesByFilter(String city, String county, List<String> villages, List<String> zipcodes,
                                                                          Integer min, Integer max, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(requestedMySpotZones.region.city.eq(city));
        }
        if (county != null) {
            whereCause.and(requestedMySpotZones.region.county.eq(county));
        }
        if (villages != null && !villages.isEmpty()) {
            whereCause.and(requestedMySpotZones.region.village.in(villages));
        }
        if (zipcodes != null && !zipcodes.isEmpty()) {
            whereCause.and(requestedMySpotZones.region.zipcode.in(zipcodes));
        }
        if (min != null) {
            whereCause.and(requestedMySpotZones.waitingUserCount.loe(min));
        }
        if (max != null) {
            whereCause.and(requestedMySpotZones.waitingUserCount.goe(max));
        }

        int offset = limit * (page - 1);

        QueryResults<RequestedMySpotZones> results = queryFactory.selectFrom(requestedMySpotZones)
                .leftJoin(requestedMySpotZones)
                .where(whereCause)
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public RequestedMySpotZones findRequestedMySpotZoneByZipcode(String zipcode) {
        return queryFactory.selectFrom(requestedMySpotZones)
                .where(requestedMySpotZones.region.zipcode.eq(zipcode))
                .fetchOne();
    }

    public List<RequestedMySpotZones> findRequestedMySpotZonesByIds(List<BigInteger> ids) {
        return queryFactory.selectFrom(requestedMySpotZones)
                .where(requestedMySpotZones.id.in(ids))
                .fetch();
    }

    public List<RequestedMySpotZones> findAlreadyExistMySpotZone() {
        return queryFactory.selectFrom(requestedMySpotZones)
                .leftJoin(requestedMySpotZones.region, region)
                .where(requestedMySpotZones.region.eq(region), region.mySpotZoneIds.isNotNull())
                .fetch();
    }

}
