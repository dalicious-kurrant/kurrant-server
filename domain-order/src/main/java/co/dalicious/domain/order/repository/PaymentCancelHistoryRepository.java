package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.PaymentCancelHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface PaymentCancelHistoryRepository extends JpaRepository<PaymentCancelHistory, BigInteger> {
}
