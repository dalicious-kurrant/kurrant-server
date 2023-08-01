package co.dalicious.domain.board.repository;

import co.dalicious.domain.board.entity.BackOfficeNotice;
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

import static co.dalicious.domain.board.entity.QBackOfficeNotice.backOfficeNotice;
import static co.dalicious.domain.board.entity.QClientNotice.clientNotice;
import static co.dalicious.domain.board.entity.QMakersNotice.makersNotice;

@Repository
@RequiredArgsConstructor
public class QBackOfficeNoticeRepository {

    private final JPAQueryFactory queryFactory;

    public Page<? extends BackOfficeNotice> findAllByParameters(BigInteger makersId, List<BigInteger> groupIds, Boolean isStatus, Boolean isAlarmTalk, BoardType type, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(groupIds != null && !groupIds.isEmpty()) {
            List<Tuple> groupIdResults = queryFactory.select(backOfficeNotice.id, clientNotice.groupIds).from(backOfficeNotice).fetch();
            List<BigInteger> noticeIds = groupIdResults.stream()
                    .filter(v -> v.get(clientNotice.groupIds) != null && v.get(clientNotice.groupIds).stream().anyMatch(groupIds::contains))
                    .map(v -> v.get(backOfficeNotice.id))
                    .toList();

            whereCause.and(backOfficeNotice.id.in(noticeIds));
        }
        if(makersId != null) {
            whereCause.and(makersNotice.makersId.eq(makersId));
        }
        if(type != null) {
            whereCause.and(backOfficeNotice.boardType.eq(type));
        }
        if(isStatus != null) {
            whereCause.and(backOfficeNotice.isStatus.eq(isStatus));
        }
        if(isAlarmTalk != null) {
            whereCause.and(backOfficeNotice.isAlarmTalk.eq(isAlarmTalk));
        }

        QueryResults<BackOfficeNotice> results = queryFactory.selectFrom(backOfficeNotice)
                .leftJoin(makersNotice).on(backOfficeNotice.id.eq(makersNotice.id))
                .leftJoin(clientNotice).on(backOfficeNotice.id.eq(clientNotice.id))
                .where(whereCause)
                .orderBy(backOfficeNotice.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<MakersNotice> findMakersNoticeAllByMakersIdAndType(BigInteger makersId, BoardType type, Pageable pageable) {
        QueryResults<MakersNotice> results = queryFactory.selectFrom(makersNotice)
                .where(makersNotice.boardType.eq(type), makersNotice.makersId.isNull(), makersNotice.makersId.eq(makersId))
                .orderBy(makersNotice.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
