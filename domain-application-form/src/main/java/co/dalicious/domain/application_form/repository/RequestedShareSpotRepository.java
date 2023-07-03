package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedShareSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface RequestedShareSpotRepository extends JpaRepository<RequestedShareSpot, BigInteger> {
}