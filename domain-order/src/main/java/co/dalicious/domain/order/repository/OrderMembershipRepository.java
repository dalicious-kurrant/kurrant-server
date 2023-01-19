package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.user.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderMembershipRepository extends JpaRepository<OrderMembership, Long> {
    Optional<OrderMembership> findOneByMembership(Membership membership);
    @Query("SELECT om FROM OrderMembership om WHERE om.membership IN :memberships")
    List<OrderMembership> findAllByMembership(@Param("memberships") List<Membership> memberships);

}
