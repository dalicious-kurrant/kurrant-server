package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.delivery.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DriverRepository extends JpaRepository<Driver, BigInteger> {
    Optional<Driver> findByCode(String code);
}