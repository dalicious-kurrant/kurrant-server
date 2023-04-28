package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, BigInteger> {
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.order o JOIN FETCH o.user WHERE oi.id IN :ids")
    List<OrderItem> findAllByIds(@Param("ids") List<BigInteger> ids);
}