package co.dalicious.domain.review.repository;


import co.dalicious.domain.review.entity.Like;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static co.dalicious.domain.review.entity.QLike.like;

import java.math.BigInteger;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QLikeRepository {

    private final JPAQueryFactory queryFactory;


    public List<Like> checkLike(BigInteger userId, BigInteger reviewId) {
        return queryFactory.selectFrom(like)
                .where(like.user.id.eq(userId),
                        like.reviewId.eq(reviewId))
                .fetch();
    }

    public Like foodReviewLikeCheckByUserId(BigInteger userId, BigInteger reviewId) {
        return queryFactory.selectFrom(like)
                .where(like.user.id.eq(userId),
                        like.reviewId.eq(reviewId))
                .fetchOne();
    }
}
