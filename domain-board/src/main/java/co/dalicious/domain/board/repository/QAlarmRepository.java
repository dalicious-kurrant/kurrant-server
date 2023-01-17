package co.dalicious.domain.board.repository;

import co.dalicious.domain.board.entity.Alarm;
import co.dalicious.domain.board.entity.QAlarm;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.board.entity.QAlarm.alarm;

@Repository
@RequiredArgsConstructor
public class QAlarmRepository {

    private final JPAQueryFactory queryFactory;


    public List<Alarm> findAllByUserId(BigInteger id) {
        return queryFactory.selectFrom(alarm)
                .where(alarm.user.id.eq(id))
                .fetch();
    }

    public Long deleteAllAlarm(BigInteger id) {
        return queryFactory.delete(alarm)
                .where(alarm.user.id.eq(id))
                .execute();
    }
}
