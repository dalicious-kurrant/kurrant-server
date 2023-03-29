package co.dalicious.domain.paycheck.repository;

import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface MakersPaycheckRepository extends JpaRepository<MakersPaycheck, BigInteger> {
    List<MakersPaycheck> findAllByOrderByCreatedDateTimeDesc();
    List<MakersPaycheck> findAllByIdIn(List<BigInteger> ids);
}