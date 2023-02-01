package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItemDailyFoodGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface OrderItemDailyFoodGroupRepository extends JpaRepository<OrderItemDailyFoodGroup, BigInteger> {
}
