package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface OrderCartRepository extends JpaRepository<OrderCart, BigInteger> {
    OrderCart findByUserId(BigInteger id);
}
