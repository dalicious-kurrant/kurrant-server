package co.dalicious.domain.review.repository;

import co.dalicious.domain.review.entity.Keyword;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static co.dalicious.domain.review.entity.QKeyword.keyword;

import java.math.BigInteger;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QKeywordRepository {

    private final JPAQueryFactory queryFactory;


    public List<String> findAllByFoodId(BigInteger foodId) {
        return queryFactory.select(keyword.name)
                .from(keyword)
                .where(keyword.food.id.eq(foodId))
                .orderBy(keyword.count.desc())
                .fetch();
    }
}
