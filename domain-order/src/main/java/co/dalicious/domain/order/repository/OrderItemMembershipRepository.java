package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItemMembership;
import co.dalicious.domain.user.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemMembershipRepository extends JpaRepository<OrderItemMembership, Long> {
    Optional<OrderItemMembership> findOneByMembership(Membership membership);
    @Query("SELECT om FROM OrderItemMembership om WHERE om.membership IN :memberships")
    List<OrderItemMembership> findAllByMembership(@Param("memberships") List<Membership> memberships);

}
