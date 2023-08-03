package co.dalicious.domain.board.repository;

import co.dalicious.domain.board.entity.BackOfficeNotice;
import co.dalicious.domain.board.entity.ClientNotice;
import co.dalicious.domain.board.entity.MakersNotice;
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

import static co.dalicious.domain.board.entity.QClientNotice.clientNotice;
import static co.dalicious.domain.board.entity.QMakersNotice.makersNotice;

@Repository
@RequiredArgsConstructor
public class QBackOfficeNoticeRepository {

    private final JPAQueryFactory queryFactory;

    public Page<MakersNotice> findAllByParameters(BigInteger makersId, Boolean isStatus, Boolean isAlarmTalk, BoardType type, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(makersId != null) {
            whereCause.and(makersNotice.makersId.eq(makersId));
        }
        if(type != null) {
            whereCause.and(makersNotice.boardType.eq(type));
        }
        if(isStatus != null) {
            whereCause.and(makersNotice.isStatus.eq(isStatus));
        }
        if(isAlarmTalk != null) {
            whereCause.and(makersNotice.isAlarmTalk.eq(isAlarmTalk));
        }

        QueryResults<MakersNotice> results = queryFactory.selectFrom(makersNotice)
                .where(whereCause)
                .orderBy(makersNotice.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<ClientNotice> findAllByParameters(List<BigInteger> groupIds, Boolean isStatus, Boolean isAlarmTalk, BoardType type, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(groupIds != null && !groupIds.isEmpty()) {
            List<Tuple> groupIdResults = queryFactory.select(clientNotice.id, clientNotice.groupIds).from(clientNotice).fetch();
            List<BigInteger> noticeIds = groupIdResults.stream()
                    .filter(v -> v.get(clientNotice.groupIds) != null && v.get(clientNotice.groupIds).stream().anyMatch(groupIds::contains))
                    .map(v -> v.get(clientNotice.id))
                    .toList();

            whereCause.and(clientNotice.id.in(noticeIds));
        }
        if(type != null) {
            whereCause.and(clientNotice.boardType.eq(type));
        }
        if(isStatus != null) {
            whereCause.and(clientNotice.isStatus.eq(isStatus));
        }
        if(isAlarmTalk != null) {
            whereCause.and(clientNotice.isAlarmTalk.eq(isAlarmTalk));
        }

        QueryResults<ClientNotice> results = queryFactory.selectFrom(clientNotice)
                .where(whereCause)
                .orderBy(clientNotice.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<MakersNotice> findMakersNoticeAllByMakersIdAndType(BigInteger makersId, List<BoardType> type, Pageable pageable) {
        QueryResults<MakersNotice> results = queryFactory.selectFrom(makersNotice)
                .where(makersNotice.boardType.in(type), makersNotice.isStatus.isTrue(), makersNotice.makersId.eq(makersId).or(makersNotice.makersId.isNull()))
                .orderBy(makersNotice.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<ClientNotice> findClientNoticeAllByClientIdAndType(BigInteger groupId, List<BoardType> type, Pageable pageable) {
        List<Tuple> groupIdResults = queryFactory.select(clientNotice.id, clientNotice.groupIds).from(clientNotice).fetch();
        List<BigInteger> noticeIds = groupIdResults.stream()
                .filter(v -> (v.get(clientNotice.groupIds) != null && v.get(clientNotice.groupIds).contains(groupId)) || v.get(clientNotice.groupIds) == null || v.get(clientNotice.groupIds).isEmpty())
                .map(v -> v.get(clientNotice.id))
                .toList();

        QueryResults<ClientNotice> results = queryFactory.selectFrom(clientNotice)
                .where(clientNotice.boardType.in(type), clientNotice.isStatus.isTrue(), clientNotice.id.in(noticeIds))
                .orderBy(clientNotice.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
