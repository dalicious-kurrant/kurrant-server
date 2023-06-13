package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.delivery.entity.DeliveryInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface DeliveryInstanceRepository extends JpaRepository<DeliveryInstance, BigInteger> {
}