package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, BigInteger> {
}
