package co.dalicious.domain.review.repository;

import co.dalicious.domain.order.entity.OrderItem;
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

    public void plusKeyword(List<String> keywordList, BigInteger foodId, String content) {
        //해당되는 키워드에 +1
        for (String word : keywordList){
            if (content.contains(word)){
                queryFactory.update(keyword)
                        .set(keyword.count, keyword.count.add(1))
                        .where(keyword.food.id.eq(foodId), keyword.name.eq(word))
                        .execute();
            }
        }

    }
}
