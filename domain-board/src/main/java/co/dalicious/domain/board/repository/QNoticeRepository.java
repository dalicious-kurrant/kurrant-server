package co.dalicious.domain.board.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import static co.dalicious.domain.board.entity.QNotice.notice;

@Repository
@RequiredArgsConstructor
public class QNoticeRepository {

    public final JPAQueryFactory queryFactory;

    public Object findAllByType(Integer type) {
        return queryFactory.selectFrom(notice)
                .where(notice.Type.eq(type))
                .fetch();
    }
}
