package co.dalicious.domain.order.repository;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.order.entity.Cart;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartDailyFood, BigInteger> {

    List<Cart> findAllByUserAndSpot(User user, Spot spot);
}
