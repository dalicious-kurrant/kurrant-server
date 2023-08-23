package co.dalicious.domain.review.repository;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.review.dto.AverageAndTotalCount;
import co.dalicious.domain.review.dto.SelectAppReviewByUserDto;
import co.dalicious.domain.review.dto.SelectCommentByReviewDto;
import co.dalicious.domain.review.entity.AdminComments;
import co.dalicious.domain.review.entity.MakersComments;
import co.dalicious.domain.review.entity.QComments;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.spec.PSource;
import java.beans.Expression;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.order.entity.QOrder.order;
import static co.dalicious.domain.food.entity.QFood.food;
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
                                         Boolean forMakers, Boolean isMakersComment, Boolean isAdminComment, Integer limit, Integer page, Pageable pageable) {
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

        if(forMakers != null){
            filter.and(reviews.forMakers.eq(forMakers));
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
                .leftJoin(reviews.orderItem, orderItem).fetchJoin()
                .leftJoin(orderItem.order, order).fetchJoin()
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

    public List<SelectAppReviewByUserDto> findAllByUser(User user) {
        NumberExpression<BigInteger> getDailyFoodId = Expressions.cases().when(reviews.food.isNotNull())
                .then(orderItemDailyFood.dailyFood.id).otherwise(BigInteger.valueOf(0));
        LiteralExpression<String> getMakersName = Expressions.cases().when(reviews.orderItem.instanceOf(OrderItemDailyFood.class))
                .then(orderItemDailyFood.dailyFood.food.makers.name).otherwise("");
        LiteralExpression<String> getItemName = Expressions.cases().when(reviews.orderItem.instanceOf(OrderItemDailyFood.class))
                .then(orderItemDailyFood.dailyFood.food.name).otherwise("");

        List<SelectAppReviewByUserDto> results = queryFactory.select(Projections.fields(SelectAppReviewByUserDto.class,
                        reviews.id.as("reviewId"), getDailyFoodId.as("dailyFoodId"), reviews.content, reviews.satisfaction,
                        reviews.createdDateTime.as("createDate"), reviews.updatedDateTime.as("updateDate"), reviews.forMakers,
                        getMakersName.as("makersName"), getItemName.as("itemName"), reviews.count().as("count"), reviews.images))
                .from(reviews)
                .innerJoin(orderItemDailyFood).on(reviews.orderItem.id.eq(orderItemDailyFood.id))
                .where(reviews.isDelete.ne(true), reviews.user.eq(user))
                .orderBy(reviews.createdDateTime.desc())
                .distinct()
                .fetch();

        Set<BigInteger> getReviewIdSet = results.stream().map(SelectAppReviewByUserDto::getReviewId).collect(Collectors.toSet());
        List<Tuple> getCommentByReview = findAllCommentByReview(getReviewIdSet);

        for (SelectAppReviewByUserDto result : results) {
            result.setCommentList(getCommentByReview.stream()
                    .filter(v -> Objects.requireNonNull(v.get(0, BigInteger.class)).equals(result.getReviewId()))
                    .map(v -> v.get(1, SelectCommentByReviewDto.class))
                    .toList()
            );
        }

        return results;
    }

    private List<Tuple> findAllCommentByReview(Set<BigInteger> reviewIds) {
        LiteralExpression<String> getCommentWriter = Expressions.cases().when(comments.instanceOf(MakersComments.class))
                .then("makers").otherwise("admin");
        return queryFactory.select(comments.reviews.id, Projections.fields(SelectCommentByReviewDto.class,
                        comments.id, getCommentWriter.as("writer"), comments.content,
                        comments.createdDateTime.as("createDate"), comments.updatedDateTime.as("updateDate")))
                .from(comments)
                .where(comments.isDelete.isFalse(), comments.reviews.id.in(reviewIds))
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

    public List<Reviews> findAllByIds(Collection<BigInteger> ids) {
        return queryFactory.selectFrom(reviews)
                .where(reviews.id.in(ids))
                .fetch();
    }

    public List<Reviews> findAllByfoodIds(Collection<BigInteger> ids) {
        return queryFactory.selectFrom(reviews)
                .where(reviews.food.id.in(ids),
                        reviews.forMakers.eq(Boolean.FALSE)) //사장님만 보이기는 제외
                .fetch();
    }

    public Map<BigInteger, Pair<Double, Long>> getStarAverage(Collection<BigInteger> foodIds) {
        List<Tuple> results = queryFactory.select(food.id, reviews.satisfaction.avg(), reviews.id.count())
                .from(reviews)
                .innerJoin(reviews.food, food)
                .where(food.id.in(foodIds))
                .groupBy(food.id)
                .fetch();
        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(food.id),
                        tuple -> Pair.of(tuple.get(reviews.satisfaction.avg()), tuple.get(reviews.id.count()))
                ));
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


    public Page<Reviews> findAllByFoodIdSort(BigInteger id, Integer photo, String star,String keyword, Pageable pageable, Integer sort) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if(photo != null && photo != 0) {
            whereClause.and(reviews.images.isNotEmpty());
        }
        if(star != null && star.length() != 0) {
            whereClause.and(starFilter(star));
        }
        if(keyword != null && !keyword.equals("")) {
            whereClause.and(keywordFilter(keyword));
        }

        if (sort == 0){ //별점순
            QueryResults<Reviews> result = queryFactory.selectFrom(reviews)
                    .where(reviews.food.id.eq(id), whereClause, reviews.forMakers.eq(false))
                    .orderBy(reviews.satisfaction.desc(),
                            reviews.createdDateTime.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchResults();
            return new PageImpl<>(result.getResults(), pageable, result.getTotal());
        }
        if (sort == 1){    //최신순
            QueryResults<Reviews> result = queryFactory.selectFrom(reviews)
                 .where(reviews.food.id.eq(id), whereClause, reviews.forMakers.eq(false))
                 .orderBy(reviews.createdDateTime.desc(),
                         reviews.satisfaction.desc())
                 .offset(pageable.getOffset())
                 .limit(pageable.getPageSize())
                 .fetchResults();
            return new PageImpl<>(result.getResults(), pageable, result.getTotal());
        }
        //추천순
        QueryResults<Reviews> result = queryFactory.selectFrom(reviews)
                .where(reviews.food.id.eq(id), whereClause, reviews.forMakers.eq(false))
                .orderBy(reviews.good.desc(),
                        reviews.createdDateTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    //별점필터
    private BooleanExpression starFilter(String starFilter){
        List<Integer> stars = new ArrayList<>();
        List<String> list = Arrays.stream(starFilter.split(",")).toList();
        for (String star : list){
            stars.add(Integer.parseInt(star));
        }
        return reviews.satisfaction.in(stars);
    }

    //키워드필터
    private BooleanExpression keywordFilter(String keywordFilter){
        return reviews.content.contains(keywordFilter);
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

    public Page<Reviews> findAllByFoodId(BigInteger foodId, Pageable pageable, Integer sort) {

        if (sort == 0){ //별점순, 같으면 최신순
            QueryResults<Reviews> reviewsList = queryFactory.selectFrom(reviews)
                    .where(reviews.food.id.eq(foodId), reviews.forMakers.eq(false))
                    .orderBy(reviews.satisfaction.desc(),
                            reviews.createdDateTime.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .fetchResults();


            return new PageImpl<>(reviewsList.getResults(), pageable, reviewsList.getTotal());
        }

        if (sort == 1){ //최신순, 같으면 별점순
            QueryResults<Reviews> reviewsList = queryFactory.selectFrom(reviews)
                    .where(reviews.food.id.eq(foodId), reviews.forMakers.eq(false))
                    .orderBy(reviews.createdDateTime.desc(),
                            reviews.satisfaction.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .fetchResults();


            return new PageImpl<>(reviewsList.getResults(), pageable, reviewsList.getTotal());
        }

        //추천순 같으면 최신순
        QueryResults<Reviews> reviewsList = queryFactory.selectFrom(reviews)
                .where(reviews.food.id.eq(foodId), reviews.forMakers.eq(false))
                .orderBy(reviews.good.desc(),
                        reviews.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();


        return new PageImpl<>(reviewsList.getResults(), pageable, reviewsList.getTotal());

    }

    public Long findKeywordCount(String name, BigInteger foodId) {
        return queryFactory.select(reviews.count())
                .from(reviews)
                .where(reviews.content.contains(name),
                        reviews.food.id.eq(foodId))
                .fetchOne();
    }

    public AverageAndTotalCount findAllByFoodIdPageableLess(BigInteger foodId) {
        AverageAndTotalCount averageAndTotalCount = new AverageAndTotalCount();
        double total = 0.0;

        List<Reviews> reviewsList = queryFactory.selectFrom(reviews)
                .where(reviews.food.id.eq(foodId))
                .fetch();

        for (Reviews reviews: reviewsList){
            total += (double) reviews.getSatisfaction();
        }

        if (!reviewsList.isEmpty()){
            double totalTemp = total / reviewsList.size();
            averageAndTotalCount.setReviewAverage(Math.round(totalTemp * 100) / 100.0);
            averageAndTotalCount.setTotalCount(reviewsList.size());
        }

        return averageAndTotalCount;
    }

    public List<Reviews> findAllByfoodIdsAndForMakers(BigInteger foodId) {
        return queryFactory.selectFrom(reviews)
                .where(reviews.food.id.eq(foodId), reviews.forMakers.eq(Boolean.FALSE))
                .fetch();
    }

    public List<Reviews> findAllByFoodIdForStar(BigInteger foodId) {
        return queryFactory.selectFrom(reviews)
                .where(reviews.food.id.eq(foodId), reviews.forMakers.eq(Boolean.FALSE))
                .fetch();
    }
}
