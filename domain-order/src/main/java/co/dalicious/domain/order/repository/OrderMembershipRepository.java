package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface OrderMembershipRepository extends JpaRepository<OrderMembership, Long> {
    List<OrderMembership> findAllByUser(User user);
}
