package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.delivery.entity.DeliveryInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Optional;

public interface DeliveryInstanceRepository extends JpaRepository<DeliveryInstance, BigInteger> {
    @Query("SELECT d FROM DeliveryInstance d LEFT JOIN FETCH d.dailyFoodDeliveries dfd LEFT JOIN FETCH dfd.orderItemDailyFood WHERE d.id = :id")
    Optional<DeliveryInstance> findByIdWithDailyFoodDeliveries(@Param("id") BigInteger id);
}