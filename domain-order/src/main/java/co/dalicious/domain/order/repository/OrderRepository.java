package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderType;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserAndOrderType(User user, OrderType orderType);
}
