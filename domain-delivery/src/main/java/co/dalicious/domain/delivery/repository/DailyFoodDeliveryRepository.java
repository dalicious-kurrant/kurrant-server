package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface DailyFoodDeliveryRepository extends JpaRepository<DailyFoodDelivery, BigInteger> {
}