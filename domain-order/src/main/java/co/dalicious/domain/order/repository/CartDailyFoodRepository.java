package co.dalicious.domain.order.repository;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartDailyFoodRepository extends JpaRepository<CartDailyFood, BigInteger> {
    List<CartDailyFood> findAllByUser(User user);
    Optional<CartDailyFood> findOneByUserAndDailyFood(User user, DailyFood dailyFood);
}
