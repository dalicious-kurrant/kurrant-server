package co.dalicious.domain.address.repository;

import co.dalicious.domain.address.entity.Region;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static co.dalicious.domain.address.entity.QRegion.region;


@Repository
@RequiredArgsConstructor
public class QRegionRepository {

    private final JPAQueryFactory queryFactory;

    public Region findRegionByZipcodeAndCountyAndVillage(String zipcode, String county, String village) {
        return queryFactory.selectFrom(region)
                .where(region.zipcode.eq(zipcode), region.county.contains(county), region.village.contains(village))
                .fetchFirst();
    }

    public  Map<BigInteger, String> findAllCity() {
        List<Tuple> resultList = queryFactory.select(region.id, region.city)
                .from(region)
                .where(region.mySpotZoneIds.isNotNull())
                .groupBy(region.city)
                .orderBy(region.city.asc())
                .fetch();

        Map<BigInteger, String> filterInfos = new HashMap<>();

        resultList.forEach(result -> {
            filterInfos.put(result.get(region.id), result.get(region.city));
        });

        return filterInfos;
    }

    public Map<BigInteger, String> findAllCountyByCity(String city) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(region.city.eq(city));
        }

        List<Tuple> resultList = queryFactory.select(region.id, region.county)
                .from(region)
                .where(region.mySpotZoneIds.isNotNull(), whereCause)
                .groupBy(region.county)
                .orderBy(region.county.asc())
                .fetch();

        Map<BigInteger, String> filterInfos = new HashMap<>();

        resultList.forEach(result -> {
            filterInfos.put(result.get(region.id), result.get(region.city));
        });
        return filterInfos;
    }

    public  Map<BigInteger, String> findAllVillageByCounty(String city, String county) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(region.city.eq(city));
        }
        if(county != null) {
            whereCause.and(region.county.eq(county));
        }

        List<Tuple> resultList = queryFactory.select(region.id, region.village)
                .from(region)
                .where(region.mySpotZoneIds.isNotNull(), whereCause)
                .groupBy(region.village)
                .orderBy(region.village.asc())
                .fetch();

        Map<BigInteger, String> filterInfos = new HashMap<>();

        resultList.forEach(result -> {
            filterInfos.put(result.get(region.id), result.get(region.city));
        });

        return filterInfos;
    }

    public Map<BigInteger, String> findAllZipcodeByCityAndCountyAndVillage(String city, String county, List<String> villages) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(region.city.eq(city));
        }
        if(county != null) {
            whereCause.and(region.county.eq(county));
        }
        if(villages != null) {
            whereCause.and(region.village.in(villages));
        }

        List<Tuple> resultList = queryFactory.select(region.id, region.zipcode)
                .from(region)
                .where(region.mySpotZoneIds.isNotNull(), whereCause)
                .groupBy(region.zipcode)
                .orderBy(region.zipcode.asc())
                .fetch();

        Map<BigInteger, String> filterInfos = new HashMap<>();

        resultList.forEach(result -> {
            filterInfos.put(result.get(region.id), result.get(region.city));
        });

        return filterInfos;
    }

    public String findCityNameById(BigInteger id) {
        return queryFactory.select(region.city)
                .from(region)
                .where(region.id.eq(id))
                .fetchOne();
    }

    public String findCountyNameById(BigInteger id) {
        return queryFactory.select(region.county).from(region)
                .where(region.id.eq(id))
                .fetchOne();
    }

    public List<String> findVillageNameById(List<BigInteger> ids) {
        return queryFactory.select(region.village).from(region)
                .where(region.id.in(ids))
                .fetch();
    }

    public List<String> findZipcodeById(List<BigInteger> ids) {
        return queryFactory.select(region.zipcode).from(region)
                .where(region.id.in(ids))
                .fetch();
    }

    public List<Region> findRegionByZipcodesAndCountiesAndVillages(List<String> zipcodes, List<String> counties, List<String> villages) {

        BooleanBuilder whereCuase = new BooleanBuilder();

        counties.forEach(county -> whereCuase.or(region.county.contains(county)));
        villages.forEach(village -> whereCuase.or(region.village.contains(village)));

        List<Region> resultByZipcodes = queryFactory.selectFrom(region)
                .where(region.zipcode.in(zipcodes), whereCuase)
                .fetch();

        return resultByZipcodes;
    }

    public List<Region> findRegionByMySpotZone(List<BigInteger> mySpotZoneIds) {
        return queryFactory.selectFrom(region)
                .where(region.mySpotZoneIds.in(mySpotZoneIds))
                .fetch();
    }

    public List<Region> findRegionByMySpotZoneId(BigInteger mySpotZoneId) {
        return queryFactory.selectFrom(region)
                .where(region.mySpotZoneIds.eq(mySpotZoneId))
                .fetch();
    }
}
