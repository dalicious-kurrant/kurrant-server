package co.dalicious.domain.board.repository;

import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.entity.enums.BoardType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static co.dalicious.domain.board.entity.QNotice.notice;

@Repository
@RequiredArgsConstructor
public class QNoticeRepository {

    public final JPAQueryFactory queryFactory;

    public List<Notice> findPopupNotice() {
        return queryFactory.selectFrom(notice)
                .where(notice.isStatus.isTrue(), notice.boardType.in(BoardType.POPUP))
                .fetch();
    }

    public List<Notice> findAllByTypeAndGroup(BoardType type, BigInteger groupId) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(groupId != null) {
            List<Tuple> groupIdResults = queryFactory.select(notice.id, notice.groupIds).from(notice).fetch();
            List<BigInteger> noticeIds = groupIdResults.stream()
                    .filter(v -> v.get(notice.groupIds) != null && v.get(notice.groupIds).contains(groupId))
                    .map(v -> v.get(notice.id))
                    .toList();

            whereCause.and(notice.id.in(noticeIds));
        }

        return queryFactory.selectFrom(notice)
                .where(notice.boardType.eq(type),notice.isStatus.isTrue())
                .fetch();
    }

    public List<Notice> findAllSpotNotice(BigInteger spotId) {
        return queryFactory.selectFrom(notice)
                .where(notice.boardType.eq(BoardType.SPOT),
                        notice.groupIds.contains(spotId))
                .fetch();
    }

    public List<Notice> findAllByIds(Set<BigInteger> ids) {
        return queryFactory.selectFrom(notice)
                .where(notice.id.in(ids))
                .fetch();
    }

    public Page<Notice> findAllByParameters(List<BigInteger> groupIds, BoardType boardType, Boolean isStatus, Boolean isPushAlarm, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(groupIds != null && !groupIds.isEmpty()) {
            List<Tuple> groupIdResults = queryFactory.select(notice.id, notice.groupIds).from(notice).fetch();
            List<BigInteger> noticeIds = groupIdResults.stream()
                    .filter(v -> v.get(notice.groupIds) != null && v.get(notice.groupIds).stream().anyMatch(groupIds::contains))
                    .map(v -> v.get(notice.id))
                    .toList();

            whereCause.and(notice.id.in(noticeIds));
        }
        if(boardType != null) {
            whereCause.and(notice.boardType.eq(boardType));
        }
        if(isStatus != null) {
            whereCause.and(notice.isStatus.eq(isStatus));
        }
        if(isPushAlarm != null) {
            whereCause.and(notice.isPushAlarm.eq(isPushAlarm));
        }

        QueryResults<Notice> results = queryFactory.selectFrom(notice)
                .where(whereCause)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
