package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface OrderDailyFoodRepository extends JpaRepository<OrderDailyFood, BigInteger> {
}