package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.PresetDailyFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface PresetDailyFoodRepository extends JpaRepository<PresetDailyFood, BigInteger> {
}
