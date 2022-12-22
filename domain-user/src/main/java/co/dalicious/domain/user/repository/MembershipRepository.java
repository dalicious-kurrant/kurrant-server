package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByUserOrderByEndDateDesc(@NotNull User user);
    List<Membership> findAll(Specification<Membership> specification, Sort sort);
    List<Membership> findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(User user, LocalDate givenDate, LocalDate givenDate2);
}
