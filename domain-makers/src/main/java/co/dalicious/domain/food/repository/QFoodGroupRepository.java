package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.FoodGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static co.dalicious.domain.food.entity.QFoodGroup.foodGroup;

@Repository
@RequiredArgsConstructor
public class QFoodGroupRepository {
    private final JPAQueryFactory queryFactory;

    public List<FoodGroup> findAllByIds(Set<BigInteger> ids) {
        return queryFactory.selectFrom(foodGroup)
                .where(foodGroup.id.in(ids))
                .fetch();
    }
}
