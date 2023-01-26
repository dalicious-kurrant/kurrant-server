package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItemDailyFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface OrderItemDailyFoodRepository extends JpaRepository<OrderItemDailyFood, BigInteger> {
}