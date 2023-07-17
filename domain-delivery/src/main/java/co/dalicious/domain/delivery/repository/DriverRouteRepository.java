package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.delivery.entity.DriverRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface DriverRouteRepository extends JpaRepository<DriverRoute, BigInteger> {
}