package co.dalicious.domain.order.repository;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.OrderCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface OrderCartItemRepository extends JpaRepository<OrderCartItem, BigInteger> {

}
