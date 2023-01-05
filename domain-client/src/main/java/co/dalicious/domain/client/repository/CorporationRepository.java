package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Corporation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

public interface CorporationRepository extends JpaRepository<Corporation, BigInteger> {
    Optional<Corporation> findByName(String name);
}
