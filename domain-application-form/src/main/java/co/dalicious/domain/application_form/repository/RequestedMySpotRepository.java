package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedMySpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;


public interface RequestedMySpotRepository extends JpaRepository<RequestedMySpot, BigInteger> {
}
