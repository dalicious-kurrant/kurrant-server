package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderDailyFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface OrderDailyFoodRepository extends JpaRepository<OrderDailyFood, BigInteger> {
}