package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedCorporation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface RequestedCorporationRepository extends JpaRepository<RequestedCorporation, BigInteger> {
}
