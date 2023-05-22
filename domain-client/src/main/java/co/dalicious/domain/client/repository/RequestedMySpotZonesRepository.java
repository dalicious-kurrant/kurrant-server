package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.RequestedMySpotZones;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;


public interface RequestedMySpotZonesRepository extends JpaRepository<RequestedMySpotZones, BigInteger> {
}
