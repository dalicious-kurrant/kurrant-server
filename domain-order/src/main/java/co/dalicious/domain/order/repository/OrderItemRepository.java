package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, BigInteger> {

    List<OrderItem> findAll();
}