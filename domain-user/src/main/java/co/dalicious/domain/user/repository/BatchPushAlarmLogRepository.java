package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.BatchPushAlarmLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface BatchPushAlarmLogRepository extends JpaRepository<BatchPushAlarmLog, BigInteger> {
}
