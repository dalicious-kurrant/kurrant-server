package co.dalicious.domain.review.repository;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.review.entity.AdminComments;
import co.dalicious.domain.review.entity.MakersComments;
import co.dalicious.domain.review.entity.QComments;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.beans.Expression;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.order.entity.QOrderItem.orderItem;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;
import static co.dalicious.domain.review.entity.QComments.comments;
import static co.dalicious.domain.review.entity.QReviewGood.reviewGood;
import static co.dalicious.domain.review.entity.QReviews.reviews;

@Repository
@RequiredArgsConstructor
public class QReviewRepository {

    public final JPAQueryFactory queryFactory;

    QComments makersComments = new QComments("makers_comments");
    QComments adminComments = new QComments("admin_comments");
    public List<Reviews> findByUserAndOrderItem(User user, OrderItem orderItem) {
        return queryFactory
                .selectFrom(reviews)
                .where(reviews.user.eq(user),
                        reviews.orderItem.eq(orderItem))
                .fetch();
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
        if(orderCode != null && orderItemName != null) {
            filter.and(orderItem.order.code.containsIgnoreCase(orderCode))
                    .or(reviews.food.name.containsIgnoreCase(orderItemName));
        }
        if(userName != null) {
            filter.and(reviews.user.name.containsIgnoreCase(userName));
        }
        if(isReport != null) {
            filter.and(reviews.isReports.eq(isReport));
        }
        if(isMakersComment != null) {
            if(isMakersComment){
                filter.and(comments.instanceOf(MakersComments.class));
            }
            else {
                JPQLQuery<Long> makersCommentsQuery = JPAExpressions.select(makersComments.count())
                        .from(makersComments)
                        .where(makersComments.reviews.eq(reviews), makersComments.instanceOf(MakersComments.class));
                filter.and(makersCommentsQuery.lt(Long.valueOf(1)));
            }
        }
        if(isAdminComment != null) {
            if(isAdminComment){
                filter.and(comments.instanceOf(AdminComments.class));
            }
            else {
                JPQLQuery<Long> adminCommentsQuery = JPAExpressions.select(adminComments.count())
                        .from(adminComments)
                        .where(adminComments.reviews.eq(reviews), adminComments.instanceOf(AdminComments.class));
                filter.and(adminCommentsQuery.lt(Long.valueOf(1)));
            }
        }

        int offset = limit * (page - 1);

        QueryResults<Reviews> results = queryFactory.selectFrom(reviews)
                .leftJoin(reviews.orderItem, orderItem)
                .leftJoin(orderItemDailyFood).on(orderItem.id.eq(orderItemDailyFood.id))
                .leftJoin(orderItemDailyFood.dailyFood, dailyFood)
                .leftJoin(reviews.comments, comments)
                .where(filter)
                .orderBy(reviews.createdDateTime.desc())
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

    public Page<Reviews> findAllByMakersExceptMakersComment(Makers makers, String foodName, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();
        if(foodName != null) {
            whereCause.and(reviews.food.name.containsIgnoreCase(foodName));
        }

        int offset = limit * (page - 1);

        JPQLQuery<Long> makersCommentsQuery = JPAExpressions.select(makersComments.count())
                .from(makersComments)
                .where(makersComments.reviews.eq(reviews), makersComments.instanceOf(MakersComments.class));

        QueryResults<Reviews> results = queryFactory.selectFrom(reviews)
                .where(reviews.food.makers.eq(makers),
                        makersCommentsQuery.lt(Long.valueOf(1)),
                        reviews.isDelete.ne(true),
                        reviews.isReports.ne(true),
                        whereCause)
                .orderBy(reviews.createdDateTime.desc())
                .distinct()
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<Reviews> findAllByMakers(Makers makers, String foodName, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();
        if(foodName != null) {
            whereCause.and(reviews.food.name.containsIgnoreCase(foodName));
        }

        int offset = limit * (page - 1);

        QueryResults<Reviews> results = queryFactory.selectFrom(reviews)
                .leftJoin(reviews.comments, comments)
                .where(reviews.food.makers.eq(makers), reviews.isDelete.ne(true), whereCause)
                .orderBy(reviews.createdDateTime.desc())
                .distinct()
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

    public List<Reviews> findAllByUserAndOrderItem(User user, List<OrderItem> orderItemList) {
        return  queryFactory.selectFrom(reviews)
                .where(reviews.user.eq(user), reviews.orderItem.in(orderItemList))
                .fetch();
    }

    public List<Reviews> findAllByIds(Set<BigInteger> ids) {
        return queryFactory.selectFrom(reviews)
                .where(reviews.id.in(ids))
                .fetch();
    }

    public long pendingReviewCount() {
        return queryFactory.selectFrom(reviews)
                .where(reviews.comments.isEmpty())
                .fetchCount();
    }

    public long countReviewByMakers(Makers makers, Boolean isComment) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(makers != null && isComment) {
            JPQLQuery<Long> makersCommentsQuery = JPAExpressions.select(makersComments.count())
                    .from(makersComments)
                    .where(makersComments.reviews.eq(reviews), makersComments.instanceOf(MakersComments.class));
            whereCause.and(makersCommentsQuery.lt(Long.valueOf(1)));
        }

        return queryFactory.selectFrom(reviews)
                .where(reviews.food.makers.eq(makers),
                        reviews.isDelete.ne(true),
                        reviews.isReports.ne(true),
                        whereCause)
                .fetchCount();

    }


    public Page<Reviews> findAllByFoodIdSort(BigInteger id, Integer photo, String star,String keyword, Pageable pageable) {

        QueryResults<Reviews> result = queryFactory.selectFrom(reviews)
                    .where(reviews.food.id.eq(id), photoFilter(photo), starFilter(star), keywordFilter(keyword))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    //별점필터
    private BooleanExpression starFilter(String starFilter){
        if (starFilter == null){
            return null;
        }
        List<Integer> stars = new ArrayList<>();
        List<String> list = Arrays.stream(starFilter.split(",")).toList();
        for (String star : list){
            stars.add(Integer.parseInt(star));
        }
        return reviews.satisfaction.in(stars);
    }

    //키워드필터
    private BooleanExpression keywordFilter(String keywordFilter){
        if (keywordFilter == null || keywordFilter.equals("")) return null;

        return reviews.content.contains(keywordFilter);
    }

    //포토필터
    private BooleanExpression photoFilter(Integer photo){
        if (photo == null) return null;

        return reviews.images.isNotEmpty();
    }

    public void plusLike(BigInteger reviewId) {
        queryFactory.update(reviews)
                .set(reviews.good, reviews.good.add(1))
                .where(reviews.id.eq(reviewId))
                .execute();

    }

    public void minusLike(BigInteger reviewId) {

        queryFactory.update(reviews)
                .set(reviews.good, reviews.good.subtract(1))
                .where(reviews.id.eq(reviewId))
                .execute();


    }

    public void deleteLike(BigInteger reviewId, BigInteger id) {
        queryFactory.delete(reviewGood)
                .where(reviewGood.reviewId.id.eq(reviewId),
                        reviewGood.user.id.eq(id))
                .execute();
    }

    public Page<Reviews> findAllByFoodId(BigInteger foodId, Pageable pageable) {
        QueryResults<Reviews> reviewsList = queryFactory.selectFrom(reviews)
                .where(reviews.food.id.eq(foodId))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();


        return new PageImpl<>(reviewsList.getResults(), pageable, reviewsList.getTotal());
    }

    public Integer findKeywordCount(String name, BigInteger foodId) {
        return Math.toIntExact(queryFactory.select(reviews.count())
                .from(reviews)
                .where(reviews.content.contains(name),
                        reviews.food.id.eq(foodId))
                .fetchOne());
    }

    public List<Reviews> findAllByfoodIds(Collection<BigInteger> ids) {
        return queryFactory.selectFrom(reviews)
                .where(reviews.food.id.in(ids))
                .fetch();
    }

     /*
    *   QueryResults<PointHistory> results =  jpaQueryFactory.selectFrom(pointHistory)
                .where(pointHistory.user.eq(user), pointHistory.point.ne(BigDecimal.ZERO))
                .orderBy(pointHistory.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    * */

}
