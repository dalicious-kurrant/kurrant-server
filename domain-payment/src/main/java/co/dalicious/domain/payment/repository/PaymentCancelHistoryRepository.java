package co.dalicious.domain.payment.repository;

import co.dalicious.domain.payment.entity.PaymentCancelHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface PaymentCancelHistoryRepository extends JpaRepository<PaymentCancelHistory, BigInteger> {
}
