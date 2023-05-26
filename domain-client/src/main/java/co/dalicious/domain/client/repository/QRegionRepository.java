package co.dalicious.domain.client.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static co.dalicious.domain.client.entity.QMySpotZone.mySpotZone;
import static co.dalicious.domain.client.entity.QRegion.region;

@Repository
@RequiredArgsConstructor
public class QRegionRepository {

    private final JPAQueryFactory queryFactory;

    public List<String> findAllCity() {
        return queryFactory.select(region.city)
                .from(region)
                .groupBy(region.city)
                .fetch();
    }

    public List<String> findAllCountyByCity(String city) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(region.city.eq(city));
        }

        return queryFactory.select(region.country)
                .where(whereCause)
                .groupBy(region.country)
                .fetch();
    }

    public List<String> findAllVillageByCounty(String city, String county) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(region.city.eq(city));
        }
        if(county != null) {
            whereCause.and(region.country.eq(county));
        }

        return queryFactory.select(region.village)
                .from(region)
                .where(whereCause)
                .fetch();
    }

    public List<String> findAllZipcodeByCityAndCountyAndVillage(String city, String county, List<String> villages) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(city != null) {
            whereCause.and(region.city.eq(city));
        }
        if(county != null) {
            whereCause.and(region.country.eq(county));
        }
        if(villages != null) {
            whereCause.and(region.village.in(villages));
        }

        return queryFactory.select(region.zipcodes)
                .from(region)
                .where(whereCause)
                .fetch();
    }
}
