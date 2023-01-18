package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, BigInteger> {

    List<Cart> findAllByUserId(BigInteger id);
}
