package co.dalicious.domain.board.repository;

import co.dalicious.domain.board.entity.Notice;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.board.entity.QNotice.notice;

@Repository
@RequiredArgsConstructor
public class QNoticeRepository {

    public final JPAQueryFactory queryFactory;

    public List<Notice> findAllByType(Integer type) {
        return queryFactory.selectFrom(notice)
                .where(notice.type.eq(type))
                .fetch();
    }
}
