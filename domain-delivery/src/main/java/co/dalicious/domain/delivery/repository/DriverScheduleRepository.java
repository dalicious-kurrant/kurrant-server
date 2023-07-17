package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.delivery.entity.DriverSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface DriverScheduleRepository extends JpaRepository<DriverSchedule, BigInteger> {
}