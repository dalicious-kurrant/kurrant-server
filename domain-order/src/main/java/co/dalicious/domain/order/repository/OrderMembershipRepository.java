package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.user.entity.Membership;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderMembershipRepository extends JpaRepository<OrderMembership, Long> {
    Optional<OrderMembership> findOneByMembership(Membership membership);
    List<OrderMembership> findAll(Specification<Membership> specification, Sort sort);
}
