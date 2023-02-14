package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Makers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MakersRepository extends JpaRepository<Makers, BigInteger> {
    Makers findByCode(String code);
}
