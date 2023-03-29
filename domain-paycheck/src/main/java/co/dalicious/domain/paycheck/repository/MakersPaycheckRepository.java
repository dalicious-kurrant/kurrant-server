package co.dalicious.domain.paycheck.repository;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface MakersPaycheckRepository extends JpaRepository<MakersPaycheck, BigInteger> {
    List<MakersPaycheck> findAllByOrderByCreatedDateTimeDesc();
    List<MakersPaycheck> findAllByMakersAndIdIn(Makers makers, List<BigInteger> ids);
    List<MakersPaycheck> findAllByIdIn(List<BigInteger> ids);
    List<MakersPaycheck> findAllByMakers(Makers makers);
}