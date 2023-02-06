package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.user.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface PaymentCancelHistoryRepository extends JpaRepository<PaymentCancelHistory, BigInteger> {
    List<PaymentCancelHistory> findAllByOrderOrderByCancelDateTimeDesc(Order order);

    @Query("SELECT om FROM PaymentCancelHistory om WHERE om.orderItem IN :orderItems")
    List<PaymentCancelHistory> findAllByOrderItems(@Param("orderItems") List<OrderItem> orderItems);
}
