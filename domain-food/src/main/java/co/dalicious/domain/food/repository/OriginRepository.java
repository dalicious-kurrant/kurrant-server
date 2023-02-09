package co.dalicious.domain.food.repository;

import co.dalicious.domain.makers.entity.enums.Origin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface OriginRepository extends JpaRepository<Origin, BigInteger> {
}
