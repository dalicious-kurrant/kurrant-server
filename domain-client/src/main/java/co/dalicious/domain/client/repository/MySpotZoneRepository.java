package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.MySpotZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MySpotZoneRepository extends JpaRepository<MySpotZone, BigInteger> {
}
