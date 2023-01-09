package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Origin;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.food.entity.QOrigin.origin;

@Repository
@RequiredArgsConstructor
public class QOriginRepository {
    public final JPAQueryFactory queryFactory;

    public List<Origin> findAllByFoodId(BigInteger foodId) {
              return queryFactory.selectFrom(origin)
                .where(origin.food.id.eq(foodId))
                .fetch();
    }
}
