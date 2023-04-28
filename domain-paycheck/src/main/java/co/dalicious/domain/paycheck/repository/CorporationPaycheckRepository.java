package co.dalicious.domain.paycheck.repository;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.paycheck.entity.CorporationPaycheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface CorporationPaycheckRepository extends JpaRepository<CorporationPaycheck, BigInteger> {
    List<CorporationPaycheck> findAllByIdIn(List<BigInteger> ids);
    List<CorporationPaycheck> findAllByCorporation(Corporation corporation);
    List<CorporationPaycheck> findAllByCorporationAndIdIn(Corporation corporation, List<BigInteger> ids);
}