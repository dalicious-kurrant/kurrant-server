package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.ApartmentSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface ApartmentSpotRepository extends JpaRepository<ApartmentSpot, BigInteger> {
}
