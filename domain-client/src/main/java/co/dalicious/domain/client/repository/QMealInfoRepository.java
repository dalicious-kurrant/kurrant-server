package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QMealInfoRepository {

    private final JPAQueryFactory queryFactory;

}
