package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, BigInteger> {
    Optional<Apartment> findByName(String name);
}
