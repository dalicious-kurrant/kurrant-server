package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByUserOrderByEndDateDesc(@NotNull User user);
    Membership findByUserAndStartDateBeforeAndEndDateAfter(User user, LocalDate now, LocalDate now2);
}
