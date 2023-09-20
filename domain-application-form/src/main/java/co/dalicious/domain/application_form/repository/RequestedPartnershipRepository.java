package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedPartnership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface RequestedPartnershipRepository extends JpaRepository<RequestedPartnership, BigInteger> {
}
