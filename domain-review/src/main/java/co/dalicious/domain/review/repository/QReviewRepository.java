package co.dalicious.domain.review.repository;

import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.review.entity.QReviews.reviews;

@Repository
@RequiredArgsConstructor
public class QReviewRepository {

    public final JPAQueryFactory queryFactory;

    public Reviews findByUserAndOrderItem(User user, OrderItem orderItem) {
        return queryFactory
                .selectFrom(reviews)
                .where(reviews.user.eq(user),
                        reviews.orderItem.eq(orderItem))
                .fetchOne();
    }
}
