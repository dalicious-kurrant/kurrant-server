package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
