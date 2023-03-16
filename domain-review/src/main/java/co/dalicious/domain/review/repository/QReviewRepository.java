package co.dalicious.domain.review.repository;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.QOrderItemDailyFood;
import co.dalicious.domain.review.entity.AdminComments;
import co.dalicious.domain.review.entity.MakersComments;
import co.dalicious.domain.review.entity.QComments;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.food.entity.QFood.food;
import static co.dalicious.domain.order.entity.QOrderItem.orderItem;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;
import static co.dalicious.domain.review.entity.QComments.comments;
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

    public Page<Reviews> findAllByFilter(BigInteger makersId, BigInteger orderItemId, String orderItemName, String userName, LocalDate startDate, LocalDate endDate, Boolean isReport,
                                         Boolean isMakersComment, Boolean isAdminComment, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder filter = new BooleanBuilder();

        if(startDate != null) {
            filter.and(dailyFood.serviceDate.goe(startDate));
        }
        if (endDate != null) {
            filter.and(dailyFood.serviceDate.loe(endDate));
        }
        if(makersId != null) {
            filter.and(reviews.food.makers.id.eq(makersId));
        }
        if(orderItemId != null) {
            filter.and(orderItem.id.eq(orderItemId));
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
        if(isMakersComment != null) {
            filter.and(comments.instanceOf(MakersComments.class));
        }
        if(isAdminComment != null) {
            filter.and(comments.instanceOf(AdminComments.class));
        }

        int offset = limit * (page - 1);

        QueryResults<Reviews> results = queryFactory.selectFrom(reviews)
                .leftJoin(reviews.orderItem, orderItem)
                .leftJoin(orderItemDailyFood).on(orderItem.id.eq(orderItemDailyFood.id))
                .leftJoin(orderItemDailyFood.dailyFood, dailyFood)
                .leftJoin(comments).on(reviews.comments.contains(comments))
                .where(filter)
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

//    public List<Reviews> findAllByFilter(BigInteger makersId, BigInteger orderItemId, String orderItemName, String userName, LocalDate startDate, LocalDate endDate, Boolean isReport,
//                                         Boolean isMakersComment, Boolean isAdminComment) {
//        BooleanBuilder filter = new BooleanBuilder();
//
//        if(startDate != null) {
//            filter.and(dailyFood.serviceDate.goe(startDate));
//        }
//        if (endDate != null) {
//            filter.and(dailyFood.serviceDate.loe(endDate));
//        }
//        if(makersId != null) {
//            filter.and(reviews.food.makers.id.eq(makersId));
//        }
//        if(orderItemId != null) {
//            filter.and(orderItem.id.eq(orderItemId));
//        }
//        if(userName != null) {
//            filter.and(reviews.user.name.containsIgnoreCase(userName));
//        }
//        if(orderItemName != null) {
//            filter.and(reviews.food.name.containsIgnoreCase(orderItemName));
//        }
//        if(isReport != null) {
//            filter.and(reviews.isReports.eq(isReport));
//        }
//        if(isMakersComment != null) {
//            filter.and(comments.instanceOf(MakersComments.class));
//        }
//        if(isAdminComment != null) {
//            filter.and(comments.instanceOf(AdminComments.class));
//        }
//
//        return queryFactory.selectFrom(reviews)
//                .leftJoin(reviews.orderItem, orderItem)
//                .leftJoin(orderItemDailyFood).on(orderItem.id.eq(orderItemDailyFood.id))
//                .leftJoin(orderItemDailyFood.dailyFood, dailyFood)
//                .leftJoin(comments).on(reviews.comments.contains(comments))
//                .where(filter)
//                .fetch();
//
//    }
}
