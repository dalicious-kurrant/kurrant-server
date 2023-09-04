package co.dalicious.domain.board.repository;

import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.entity.enums.BoardOption;
import co.dalicious.domain.board.entity.enums.BoardType;
import co.dalicious.domain.user.entity.UserGroup;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static co.dalicious.domain.board.entity.QNotice.notice;

@Repository
@RequiredArgsConstructor
public class QNoticeRepository {

    public final JPAQueryFactory queryFactory;

    public List<Notice> findPopupNotice(List<BigInteger> userGroups) {
        List<Tuple> popupNotice = queryFactory.select(notice.id, notice.boardType, notice.boardOption, notice.groupIds).from(notice)
                .where(notice.isStatus.isTrue(), notice.activeDate.goe(LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(7)))
                .fetch();

        popupNotice.removeAll(popupNotice.stream().filter(v -> !v.get(notice.boardOption).contains(BoardOption.POPUP)).toList());

        List<BigInteger> noticeIds = popupNotice.stream().filter(
                v -> Objects.equals(v.get(notice.boardType), BoardType.ALL) || (Objects.equals(v.get(notice.boardType), BoardType.SPOT))
        )

        return queryFactory.selectFrom(notice)
                .where(notice.id.in(noticeIds))
                .orderBy(notice.createdDateTime.desc())
                .fetch();
    }

    public Page<Notice> findAllNoticeBySpotFilter(List<BigInteger> groupIds, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(groupIds != null && !groupIds.isEmpty()) {
            List<Tuple> groupIdResults = queryFactory.select(notice.id, notice.groupIds).from(notice).fetch();
            List<BigInteger> noticeIds = groupIdResults.stream()
                    .filter(v -> v.get(notice.groupIds) != null && v.get(notice.groupIds).stream().anyMatch(groupIds::contains))
                    .map(v -> v.get(notice.id))
                    .toList();

            whereCause.and(notice.boardType.eq(BoardType.SPOT));
            whereCause.and(notice.id.in(noticeIds));
        }
        else {
            whereCause.and(notice.boardType.in(BoardType.showApp()));
            whereCause.and(notice.boardType.ne(BoardType.SPOT));
        }


        QueryResults<Notice> results = queryFactory.selectFrom(notice)
                .where(whereCause, notice.isStatus.isTrue())
                .orderBy(notice.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public List<Notice> findAllByIds(Set<BigInteger> ids) {
        return queryFactory.selectFrom(notice)
                .where(notice.id.in(ids))
                .fetch();
    }

    public Page<Notice> findAllByParameters(List<BigInteger> groupIds, BoardType boardType, Boolean isStatus, Boolean isPushAlarm, Boolean isPopup, Boolean isEvent, Pageable pageable) {
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
        if (isPopup != null) {
            List<Tuple> popupNotice = queryFactory.select(notice.id, notice.boardOption).from(notice).fetch();
            List<BigInteger> noticeIds = popupNotice.stream().filter(v -> v.get(notice.boardOption).contains(BoardOption.POPUP)).map(v -> v.get(notice.id)).toList();
            whereCause.and(notice.id.in(noticeIds));
        }
        if (isEvent != null) {
            List<Tuple> popupNotice = queryFactory.select(notice.id, notice.boardOption).from(notice).fetch();
            List<BigInteger> noticeIds = popupNotice.stream().filter(v -> v.get(notice.boardOption).contains(BoardOption.EVENT)).map(v -> v.get(notice.id)).toList();
            whereCause.and(notice.id.in(noticeIds));
        }

        QueryResults<Notice> results = queryFactory.selectFrom(notice)
                .where(whereCause)
                .orderBy(notice.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
