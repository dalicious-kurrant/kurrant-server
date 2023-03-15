package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.DailyFoodGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface DailyFoodGroupRepository extends JpaRepository<DailyFoodGroup, BigInteger> {
}