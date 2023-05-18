package co.dalicious.domain.review.repository;


import co.dalicious.domain.review.entity.Like;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

import static co.dalicious.domain.review.entity.QLike.like;

@Repository
@RequiredArgsConstructor
public class QLikeRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Like> foodReviewLikeCheckByUserId(BigInteger userId, BigInteger reviewId) {
        return Optional.ofNullable(queryFactory.selectFrom(like)
                .where(like.user.id.eq(userId),
                        like.reviewId.id.eq(reviewId))
                .fetchOne());


    }
}
