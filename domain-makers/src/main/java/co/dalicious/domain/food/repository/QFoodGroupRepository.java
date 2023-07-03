package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.FoodGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static co.dalicious.domain.food.entity.QFoodGroup.foodGroup;
import static co.dalicious.domain.food.entity.QMakersCapacity.makersCapacity;

@Repository
@RequiredArgsConstructor
public class QFoodGroupRepository {
    private final JPAQueryFactory queryFactory;

    public List<FoodGroup> findAllByIds(Set<BigInteger> ids) {
        return queryFactory.selectFrom(foodGroup)
                .where(foodGroup.id.in(ids))
                .fetch();
    }

    public List<FoodGroup> findAllByNames(Set<String> names) {
        return queryFactory.selectFrom(foodGroup)
                .where(foodGroup.name.in(names))
                .fetch();
    }

    public void deleteAllByIds(List<BigInteger> ids) {
        queryFactory.delete(makersCapacity)
                .where(foodGroup.id.in(ids))
                .execute();
    }

    public List<FoodGroup> findAllByMakersId(BigInteger makersId) {
        return queryFactory.selectFrom(foodGroup)
                .where(foodGroup.makers.id.eq(makersId))
                .fetch();
    }
}
