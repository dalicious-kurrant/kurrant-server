package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.enums.FoodStatus;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

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

    public List<Food> findByMakersAndFoodStatus(Makers makers) {
        return queryFactory
                .selectFrom(food)
                .where(food.makers.eq(makers), food.foodStatus.eq(FoodStatus.SALES))
                .fetch();
    }
    
    public Page<Food> findAllPage(Integer limit, Integer page, Pageable pageable) {
      int offset = limit * (page - 1);
      QueryResults<Food> results = queryFactory.selectFrom(food)
              .limit(limit)
              .offset(offset)
              .fetchResults();

      return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
    
    public List<Food> findByMakers(List<Makers> makers) {
      return queryFactory
              .selectFrom(food)
              .where(food.makers.in(makers))
              .fetch();
    }

    public List<Food> findByNames(Set<String> names) {
        return queryFactory.selectFrom(food)
                .where(food.name.in(names))
                .fetch();
    }
}
