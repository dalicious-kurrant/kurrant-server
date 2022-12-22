package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface OrderCartItemRepository extends JpaRepository<OrderCartItem, BigInteger> {

}
