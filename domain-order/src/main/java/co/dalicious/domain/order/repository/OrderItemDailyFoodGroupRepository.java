package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItemDailyFoodGroup;
import co.dalicious.domain.order.entity.OrderItemMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface OrderItemDailyFoodGroupRepository extends JpaRepository<OrderItemDailyFoodGroup, BigInteger> {
}
