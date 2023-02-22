package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.PresetGroupDailyFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface PresetGroupDailyFoodRepository extends JpaRepository<PresetGroupDailyFood, BigInteger> {
}
