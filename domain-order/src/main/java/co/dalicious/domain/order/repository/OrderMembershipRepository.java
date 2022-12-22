package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.user.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMembershipRepository extends JpaRepository<OrderMembership, Long> {
    OrderMembership findByMembership(Membership membership);
}
