package co.dalicious.domain.paycheck.repository;

import co.dalicious.domain.paycheck.entity.ExpectedPaycheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface ExpectedPaycheckRepository extends JpaRepository<ExpectedPaycheck, BigInteger> {
}