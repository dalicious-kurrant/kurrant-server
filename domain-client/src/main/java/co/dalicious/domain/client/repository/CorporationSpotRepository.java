package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.CorporationSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface CorporationSpotRepository extends JpaRepository<CorporationSpot, BigInteger> {
}
