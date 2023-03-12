package co.dalicious.domain.review.repository;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.QOrderItemDailyFood;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.review.entity.QReviews.reviews;

@Repository
@RequiredArgsConstructor
public class QReviewRepository {

    public final JPAQueryFactory queryFactory;
    public final EntityManager entityManager;

    public Reviews findByUserAndOrderItem(User user, OrderItem orderItem) {
        return queryFactory
                .selectFrom(reviews)
                .where(reviews.user.eq(user),
                        reviews.orderItem.eq(orderItem))
                .fetchOne();
    }

    public Reviews findByUserAndId(User user, BigInteger reviewId) {
        return queryFactory.selectFrom(reviews)
                .where(reviews.id.eq(reviewId), reviews.user.eq(user))
                .fetchOne();
    }

    public List<Reviews> findAllByFilter(BigInteger makersId, BigInteger orderItemId, String orderItemName, String userName, List<OrderItem> orderItemList, Boolean isReport) {
        BooleanBuilder filter = new BooleanBuilder();

        if(makersId != null) {
            filter.and(reviews.food.makers.id.eq(makersId));
        }
        if(orderItemId != null) {
            filter.and(reviews.orderItem.id.eq(orderItemId));
        }
        if(userName != null) {
            filter.and(reviews.user.name.containsIgnoreCase(userName));
        }
        if(orderItemName != null) {
            filter.and(reviews.food.name.containsIgnoreCase(orderItemName));
        }
        if(isReport != null) {
            filter.and(reviews.isReports.eq(isReport));
        }

        return queryFactory.selectFrom(reviews)
                .where(reviews.orderItem.in(orderItemList), filter)
                .fetch();
    }
}
