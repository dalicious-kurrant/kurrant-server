package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;


public interface RequestedMySpotZonesRepository extends JpaRepository<RequestedMySpotZones, BigInteger> {
}
