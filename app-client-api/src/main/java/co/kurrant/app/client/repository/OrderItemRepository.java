package co.kurrant.app.client.repository;

import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.dalicious.domain.order.entity.OrderDetail;

@Repository
public interface OrderItemRepository
    extends JpaRepository<OrderDetail, BigInteger>, OrderItemCustomRepository {

}
