package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.PaymentCancelHistory;
import co.dalicious.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface PaymentCancelHistoryRepository extends JpaRepository<PaymentCancelHistory, BigInteger> {
    List<PaymentCancelHistory> findAllByOrderOrderByCancelDateTimeDesc(Order order);
}
