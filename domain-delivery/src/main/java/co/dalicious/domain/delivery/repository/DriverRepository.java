package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.delivery.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, BigInteger> {
    Optional<Driver> findByCode(String code);
}