package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Origin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface OriginRepository extends JpaRepository<Origin, BigInteger> {
}
