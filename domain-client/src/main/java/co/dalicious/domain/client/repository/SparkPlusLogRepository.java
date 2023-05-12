package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.SparkPlusLog;
import co.dalicious.domain.client.entity.enums.SparkPlusLogType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SparkPlusLogRepository extends JpaRepository<SparkPlusLog, Integer> {
    Optional<SparkPlusLog> findOneBySparkPlusLogType(SparkPlusLogType sparkPlusLogType);
}