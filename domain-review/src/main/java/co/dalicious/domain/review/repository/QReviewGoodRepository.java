package co.dalicious.domain.review.repository;


import co.dalicious.domain.review.entity.ReviewGood;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

import static co.dalicious.domain.review.entity.QReviewGood.reviewGood;


@Repository
@RequiredArgsConstructor
public class QReviewGoodRepository {

    private final JPAQueryFactory queryFactory;


    public Optional<ReviewGood> foodReviewLikeCheckByUserId(BigInteger userId, BigInteger reviewId) {
        return Optional.ofNullable(queryFactory.selectFrom(reviewGood)
                .where(reviewGood.user.id.eq(userId),
                        reviewGood.reviewId.id.eq(reviewId))
                .fetchOne());
    }
}

