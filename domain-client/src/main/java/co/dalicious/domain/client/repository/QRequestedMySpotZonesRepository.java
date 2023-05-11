package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.RequestedMySpotZones;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QRequestedMySpotZonesRepository {

    private JPAQueryFactory queryFactory;

    public List<RequestedMySpotZones> findAllRequestedMySpotZonesByFilter(String city, List<String> county, List<String>)
}
