package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedMakers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface RequestedMakersRepository extends JpaRepository<RequestedMakers, BigInteger> {
}
