package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.dto.mySpotZone.filter.RequestFilterDto;
import co.dalicious.domain.client.entity.RequestedMySpotZones;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.client.entity.QRequestedMySpotZones.requestedMySpotZones;

@Repository
@RequiredArgsConstructor
public class QRequestedMySpotZonesRepository {

    private JPAQueryFactory queryFactory;

}
