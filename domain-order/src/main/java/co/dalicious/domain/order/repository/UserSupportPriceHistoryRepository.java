package co.dalicious.domain.order.repository;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface UserSupportPriceHistoryRepository extends JpaRepository<DailyFoodSupportPrice, BigInteger> {
    List<DailyFoodSupportPrice> findAllByUser(User user);

    List<DailyFoodSupportPrice> findAllByUserAndGroupAndServiceDate(User user, Group group, LocalDate serviceDate);
}
