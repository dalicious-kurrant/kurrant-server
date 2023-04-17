package co.dalicious.client.alarm.repository;

import co.dalicious.client.alarm.entity.PushAlarms;
import co.dalicious.client.alarm.entity.enums.PushStatus;
import co.dalicious.domain.user.entity.enums.PushCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static co.dalicious.client.alarm.entity.QPushAlarms.pushAlarms;

@Repository
@RequiredArgsConstructor
public class QPushAlarmsRepository {

    private final JPAQueryFactory queryFactory;

    public PushAlarms findByPushCondition(PushCondition pushCondition) {
        return queryFactory.selectFrom(pushAlarms)
                .where(pushAlarms.pushStatus.eq(PushStatus.ACTIVE),
                        pushAlarms.condition.eq(pushCondition))
                .fetchOne();
    }

}
