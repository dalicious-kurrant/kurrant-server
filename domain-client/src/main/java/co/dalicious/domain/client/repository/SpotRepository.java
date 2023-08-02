package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.enums.DiningType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface SpotRepository extends JpaRepository<Spot, BigInteger> {
    @EntityGraph(attributePaths = {"group"})
    Optional<Spot> findOneById(BigInteger id);
}
