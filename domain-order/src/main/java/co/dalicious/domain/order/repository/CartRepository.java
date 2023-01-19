package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.Cart;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, BigInteger> {

    List<Cart> findAllByUser(User user);
}
