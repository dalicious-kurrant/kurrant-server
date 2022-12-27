package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.OrderCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface OrderCartItemRepository extends JpaRepository<OrderCartItem, Integer> {

    @Query(value = "SELECT * FROM order__cart_item WHERE cart_id = ?1",nativeQuery = true)
    List<OrderCartItem> getItems(Set<Integer> singleton);

    @Query(value = "DELETE FROM order__cart_item WHERE cart_id = ?1 and food_id = ?2", nativeQuery = true)
    void deleteByFoodId(Integer id, Integer foodId);

    @Query(value = "DELETE FROM order__cart_item WHERE cart_id =?1",nativeQuery = true)
    void deleteByCartId(Integer cartId);
}
