package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.user.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface OrderMembershipRepository extends JpaRepository<OrderMembership, Long> {
    Optional<OrderMembership> findOneByMembership(Membership membership);
}
