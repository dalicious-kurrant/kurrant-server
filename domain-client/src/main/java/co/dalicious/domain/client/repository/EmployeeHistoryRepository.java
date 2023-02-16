package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.EmployeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, BigInteger> {
}
