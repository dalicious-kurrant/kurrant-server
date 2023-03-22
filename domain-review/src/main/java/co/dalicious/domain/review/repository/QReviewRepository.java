package co.dalicious.domain.review.repository;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.review.entity.AdminComments;
import co.dalicious.domain.review.entity.MakersComments;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.order.entity.QOrderItem.orderItem;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;
import static co.dalicious.domain.review.entity.QComments.comments;
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

    public Reviews findByUserAndId(User user, BigInteger reviewId) {
        return queryFactory.selectFrom(reviews)
                .where(reviews.id.eq(reviewId), reviews.user.eq(user))
                .fetchOne();
    }

    public Page<Reviews> findAllByFilter(BigInteger makersId, String orderCode, String orderItemName, String userName, LocalDate startDate, LocalDate endDate, Boolean isReport,
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
        if(orderCode != null) {
            filter.and(orderItem.order.code.containsIgnoreCase(orderCode));
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
                .leftJoin(reviews.comments, comments)
                .where(filter)
                .distinct()
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Reviews findByIdExceptedDelete(BigInteger id) {
        return queryFactory.selectFrom(reviews)
                .where(reviews.id.eq(id), reviews.isDelete.ne(true))
                .fetchOne();
    }

    public List<Reviews> findAllByUser(User user) {
        return queryFactory.selectFrom(reviews)
                .leftJoin(reviews.comments, comments)
                .where(reviews.isDelete.ne(true), reviews.user.eq(user))
                .orderBy(reviews.createdDateTime.desc())
                .distinct()
                .fetch();
    }

    public Reviews findById(BigInteger id) {
        return queryFactory.selectFrom(reviews)
                .where(reviews.id.eq(id))
                .fetchOne();
    }

    public Page<Reviews> findAllByMakersExceptMakersComment(Makers makers, Integer limit, Integer page, Pageable pageable) {

        int offset = limit * (page - 1);

        QueryResults<Reviews> results = queryFactory.selectFrom(reviews)
                .leftJoin(reviews.comments, comments)
                .where(reviews.food.makers.eq(makers),
                        reviews.comments.isEmpty().or(comments.instanceOf(AdminComments.class)),
                        reviews.isDelete.ne(true))
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<Reviews> findAllByMakers(Makers makers, Integer limit, Integer page, Pageable pageable) {

        int offset = limit * (page - 1);

        QueryResults<Reviews> results = queryFactory.selectFrom(reviews)
                .leftJoin(reviews.comments, comments)
                .where(reviews.food.makers.eq(makers), reviews.isDelete.ne(true))
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public MultiValueMap<LocalDate, Integer> getReviewScoreMap(Food food) {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate end = now.minusDays(20);

        List<Reviews> reviewsList = queryFactory.selectFrom(reviews)
                .leftJoin(reviews.orderItem, orderItem)
                .leftJoin(orderItemDailyFood).on(orderItem.id.eq(orderItemDailyFood.id))
                .leftJoin(orderItemDailyFood.dailyFood, dailyFood)
                .where(dailyFood.serviceDate.between(end, now), reviews.food.eq(food))
                .fetch();

        MultiValueMap<LocalDate, Integer> scoreMap = new LinkedMultiValueMap<>();

        for(Reviews r : reviewsList) {
            OrderItem item = r.getOrderItem();
            if(Hibernate.unproxy(item) instanceof OrderItemDailyFood o) {
                LocalDate serviceDate = o.getDailyFood().getServiceDate();
                Integer satisfaction = r.getSatisfaction();
                scoreMap.add(serviceDate, satisfaction);
            }
        }

        return scoreMap;
    }

    public void updateDefault(Reviews r) {
        queryFactory.update(reviews)
                .set(reviews.isDelete, false)
                .set(reviews.isReports, false)
                .where(reviews.eq(r))
                .execute();

    }
}
