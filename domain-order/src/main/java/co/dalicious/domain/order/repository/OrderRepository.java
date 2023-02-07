package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, BigInteger> {
    List<Order> findAllByUserOrderByCreatedDateTimeDesc(User user);
    Optional<Order> findOneByIdAndUser(BigInteger orderId, User user);
}
