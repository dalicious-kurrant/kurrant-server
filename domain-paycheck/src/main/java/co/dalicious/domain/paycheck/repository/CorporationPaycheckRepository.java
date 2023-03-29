package co.dalicious.domain.paycheck.repository;

import co.dalicious.domain.paycheck.entity.CorporationPaycheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface CorporationPaycheckRepository extends JpaRepository<CorporationPaycheck, BigInteger> {
    List<CorporationPaycheck> findAllByIdIn(List<BigInteger> ids);
}