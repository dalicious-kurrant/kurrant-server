package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.system.enums.DiningType;
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

    public List<MakersCapacity> findByMakers(Makers makers) {
        return queryFactory.selectFrom(makersCapacity)
                .where(makersCapacity.makers.id.eq(makers.getId()))
                .fetch();
    }

    public void updateDailyCapacity(Integer diningType, Integer capacity, BigInteger id) {
        queryFactory.update(makersCapacity)
                .set(makersCapacity.capacity, capacity)
                .where(makersCapacity.makers.id.eq(id),
                        makersCapacity.diningType.eq(DiningType.ofCode(diningType)))
                .execute();
    }

    public void updateDailyCapacityDiningType(Integer morning, Integer lunch, Integer dinner, String diningType, BigInteger id) {
        queryFactory.update(makersCapacity)
                .set(makersCapacity.capacity, morning)
                .where(makersCapacity.diningType.eq(DiningType.ofString(diningType)),
                        makersCapacity.makers.id.eq(id))
                .execute();

    }
}
