package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface RegionRepository extends JpaRepository<Region, BigInteger> {
}
