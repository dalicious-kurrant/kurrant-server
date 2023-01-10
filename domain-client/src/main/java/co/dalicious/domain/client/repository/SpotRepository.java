package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface SpotRepository extends JpaRepository<Spot, BigInteger> {
}
