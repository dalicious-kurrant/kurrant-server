package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface OrderCartRepository extends JpaRepository<OrderCart, BigInteger> {

    List<OrderCart> findAllByUserId(BigInteger id);
}
