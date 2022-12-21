package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderType;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserAndOrderType(User user, OrderType orderType);
}
