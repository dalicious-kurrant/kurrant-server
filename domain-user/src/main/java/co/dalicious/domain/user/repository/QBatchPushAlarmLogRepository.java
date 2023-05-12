package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.BatchPushAlarmLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.user.entity.QBatchPushAlarmLog.batchPushAlarmLog;

@Repository
@RequiredArgsConstructor
public class QBatchPushAlarmLogRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<BatchPushAlarmLog> findAllBatchAlarmLogByUserIds(List<BigInteger> userIds) {
        return jpaQueryFactory.selectFrom(batchPushAlarmLog)
                .where(batchPushAlarmLog.userId.in(userIds))
                .fetch();
    }
}
