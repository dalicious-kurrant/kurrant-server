package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PointStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static co.dalicious.domain.user.entity.QPointHistory.pointHistory;

@Repository
@RequiredArgsConstructor
public class QPointHistoryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<PointHistory> findAllByPointPolicy(PointPolicy policy) {
        return jpaQueryFactory.selectFrom(pointHistory)
                .where(pointHistory.pointPolicyId.eq(policy.getId()), pointHistory.point.eq(BigDecimal.valueOf(0)))
                .fetch();
    }

    public List<PointHistory> findAllByUserAndPointPolicy(User user, PointPolicy pointPolicy) {
        return jpaQueryFactory.selectFrom(pointHistory)
                .where(pointHistory.user.eq(user), pointHistory.pointPolicyId.eq(pointPolicy.getId()))
                .fetch();
    }

    public Page<PointHistory> findAllPointHistoryByType(User user, Pageable pageable, int type) {
        BooleanBuilder whereCause = new BooleanBuilder();
        if(type == 0) {
            whereCause.and(pointHistory.user.eq(user));
            whereCause.and(pointHistory.point.ne(BigDecimal.ZERO));
        }
        if(type == 1) {
            whereCause.and(pointHistory.user.eq(user));
            whereCause.and(pointHistory.point.ne(BigDecimal.ZERO));
            whereCause.and(pointHistory.pointStatus.in(PointStatus.rewardStatus()));
        }
        if(type == 2) {
            whereCause.and(pointHistory.user.eq(user));
            whereCause.and(pointHistory.point.ne(BigDecimal.ZERO));
            whereCause.and(pointHistory.pointStatus.in(PointStatus.userStatus()));
        }

        QueryResults<PointHistory> results =  jpaQueryFactory.selectFrom(pointHistory)
                .where(whereCause)
                .orderBy(pointHistory.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

//    public Page<PointHistory> findAllPointHistoryByRewardStatus(User user, Pageable pageable) {
//
//        QueryResults<PointHistory> results =  jpaQueryFactory.selectFrom(pointHistory)
//                .where(pointHistory.user.eq(user), pointHistory.point.ne(BigDecimal.ZERO), pointHistory.pointStatus.in(PointStatus.rewardStatus()))
//                .orderBy(pointHistory.id.desc())
//                .limit(pageable.getPageSize())
//                .offset(pageable.getOffset())
//                .fetchResults();
//
//        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
//    }
//
//    public Page<PointHistory> findAllPointHistoryByUseStatus(User user, Pageable pageable) {
//
//        QueryResults<PointHistory> results =  jpaQueryFactory.selectFrom(pointHistory)
//                .where(pointHistory.user.eq(user), pointHistory.point.ne(BigDecimal.ZERO), pointHistory.pointStatus.in(PointStatus.userStatus()))
//                .orderBy(pointHistory.id.desc())
//                .limit(pageable.getPageSize())
//                .offset(pageable.getOffset())
//                .fetchResults();
//
//        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
//    }

    public List<PointHistory> findByContentId(User user, BigInteger id, PointStatus pointStatus) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(pointStatus.equals(PointStatus.REVIEW_REWARD)) {
            whereCause.and(pointHistory.reviewId.eq(id));
        }
        if(pointStatus.equals(PointStatus.EVENT_REWARD)) {
            whereCause.and(pointHistory.boardId.eq(id));
        }
        if(pointStatus.equals(PointStatus.CANCEL)) {
            whereCause.and(pointHistory.paymentCancelHistoryId.eq(id));
        }
        if(pointStatus.equals(PointStatus.USED)) {
            whereCause.and(pointHistory.orderId.eq(id));
        }

        return jpaQueryFactory.selectFrom(pointHistory)
                .where(pointHistory.user.eq(user), whereCause)
                .fetch();
    }

    public List<PointHistory> findPointHistoryByPointStatusAndUser(User user, PointStatus pointStatus) {
        Timestamp start = Timestamp.valueOf(LocalDate.now(ZoneId.of("Asia/Seoul")).atTime(LocalTime.MIN));
        Timestamp end = Timestamp.valueOf(LocalDate.now(ZoneId.of("Asia/Seoul")).atTime(LocalTime.MAX));


        return jpaQueryFactory.selectFrom(pointHistory)
                .where(pointHistory.user.eq(user), pointHistory.pointStatus.eq(pointStatus), pointHistory.createdDateTime.between(start, end))
                .fetch();
    }
}
