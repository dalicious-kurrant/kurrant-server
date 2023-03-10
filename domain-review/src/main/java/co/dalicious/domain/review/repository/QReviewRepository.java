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

    public List<Reviews> findAllByFilter(Makers makers, Food food, User user, LocalDate start, LocalDate end) {
        BooleanBuilder filter = new BooleanBuilder();

        if(makers != null) {
            filter.and(reviews.food.makers.eq(makers));
        }
        if(food != null) {
            filter.and(reviews.food.eq(food));
        }
        if(user != null) {
            filter.and(reviews.user.eq(user));
        }

        QOrderItemDailyFood orderItemDailyFood = QOrderItemDailyFood.orderItemDailyFood;
        JPAQuery<OrderItemDailyFood> query = new JPAQuery<>(entityManager);

        List<OrderItemDailyFood> result = query
                .select(orderItemDailyFood)
                .from(orderItemDailyFood)
                .where(orderItemDailyFood.dailyFood.serviceDate.between(start, end))
                .fetch();

        return queryFactory.selectFrom(reviews)
                .where(reviews.orderItem.in(result), filter)
                .fetch();




    }
}
