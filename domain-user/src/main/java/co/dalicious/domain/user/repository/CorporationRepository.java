package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.Corporation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface CorporationRepository extends JpaRepository<Corporation, BigInteger> {
    Optional<Corporation> findByName(String name);
}
