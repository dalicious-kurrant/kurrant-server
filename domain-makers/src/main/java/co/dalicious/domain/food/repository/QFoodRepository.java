package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.food.entity.QFood.food;

@Repository
@RequiredArgsConstructor
public class QFoodRepository {

    private final JPAQueryFactory queryFactory;

    public Food findByIdAndMakers(BigInteger foodId, Makers makers) {
        return queryFactory
                .selectFrom(food)
                .where(food.id.eq(foodId), food.makers.eq(makers))
                .fetchOne();
    }

    public Food findByNameAndMakers(String name, Makers makers) {
        return queryFactory
                .selectFrom(food)
                .where(food.name.eq(name), food.makers.eq(makers))
                .fetchOne();
    }

    public List<Food> findByMakers( Makers makers) {
        return queryFactory
                .selectFrom(food)
                .where(food.makers.eq(makers))
                .fetch();
    }
}
