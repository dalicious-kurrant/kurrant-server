package co.dalicious.client.alarm.repository;

import co.dalicious.client.alarm.entity.PushAlarms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface PushAlarmRepository extends JpaRepository<PushAlarms, BigInteger> {
}
