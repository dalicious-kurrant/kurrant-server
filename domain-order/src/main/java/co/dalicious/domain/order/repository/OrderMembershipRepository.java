package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderMembership;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderMembershipRepository extends JpaRepository<OrderMembership, Long> {
}
