package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.MakersCapacity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.food.entity.QMakersCapacity.makersCapacity;

@Repository
@RequiredArgsConstructor
public class QMakersCapacityRepository {

    private final JPAQueryFactory queryFactory;


    public List<MakersCapacity> findByMakersId(BigInteger id) {
        return queryFactory.selectFrom(makersCapacity)
                .where(makersCapacity.makers.id.eq(id))
                .fetch();
    }
}
