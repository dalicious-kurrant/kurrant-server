package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.dto.filter.FilterInfo;
import co.dalicious.domain.client.entity.Region;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static co.dalicious.domain.client.entity.QRegion.region;

@Repository
@RequiredArgsConstructor
public class QRegionRepository {

    private final JPAQueryFactory queryFactory;

    public Region findRegionByZipcodeAndCountyAndVillage(String zipcode, String county, String village) {
        String village1 = village.replaceAll("동$", "");
        Region region1 = queryFactory.selectFrom(region)
                .where(region.zipcode.eq(zipcode), region.county.contains(county), region.village.contains(village1))
                .fetchFirst();

        return region1;
    }

    public List<FilterInfo> findAllCity() {
        List<Tuple> resultList = queryFactory.select(region.id, region.city)
                .from(region)
                .where(region.mySpotZone.isNotNull())
                .groupBy(region.city)
                .orderBy(region.city.asc())
                .fetch();

        List<FilterInfo> filterInfos = new ArrayList<>();

        resultList.forEach(result -> {
            FilterInfo filterInfo = new FilterInfo();

            filterInfo.setId(result.get(region.id));
            filterInfo.setName(result.get(region.city));
            filterInfos.add(filterInfo);
        });

        return filterInfos;
    }

    public List<FilterInfo> findAllCountyByCity(String city) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(region.city.eq(city));
        }

        List<Tuple> resultList = queryFactory.select(region.id, region.county)
                .from(region)
                .where(region.mySpotZone.isNotNull(), whereCause)
                .groupBy(region.county)
                .orderBy(region.county.asc())
                .fetch();

        List<FilterInfo> filterInfos = new ArrayList<>();
        resultList.forEach(result -> {
            FilterInfo filterInfo = new FilterInfo();

            filterInfo.setId(result.get(region.id));
            filterInfo.setName(result.get(region.county));
            filterInfos.add(filterInfo);
        });
        return filterInfos;
    }

    public List<FilterInfo> findAllVillageByCounty(String city, String county) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(region.city.eq(city));
        }
        if(county != null) {
            whereCause.and(region.county.eq(county));
        }

        List<Tuple> resultList = queryFactory.select(region.id, region.village)
                .from(region)
                .where(region.mySpotZone.isNotNull(), whereCause)
                .groupBy(region.village)
                .orderBy(region.village.asc())
                .fetch();

        List<FilterInfo> filterInfos = new ArrayList<>();

        resultList.forEach(result -> {
            FilterInfo filterInfo = new FilterInfo();

            filterInfo.setId(result.get(region.id));
            filterInfo.setName(result.get(region.village));
            filterInfos.add(filterInfo);
        });

        return filterInfos;
    }

    public List<FilterInfo> findAllZipcodeByCityAndCountyAndVillage(String city, String county, List<String> villages) {
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
                .where(region.mySpotZone.isNotNull(), whereCause)
                .groupBy(region.zipcode)
                .orderBy(region.zipcode.asc())
                .fetch();

        List<FilterInfo> filterInfos = new ArrayList<>();

        resultList.forEach(result -> {
            FilterInfo filterInfo = new FilterInfo();

            filterInfo.setId(result.get(region.id));
            filterInfo.setName(result.get(region.zipcode));
            filterInfos.add(filterInfo);
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

}
