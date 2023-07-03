package co.dalicious.domain.logs.repository;

import co.dalicious.domain.logs.entity.AdminLogs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface AdminLogsRepository extends JpaRepository<AdminLogs, BigInteger> {
}