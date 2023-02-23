package co.dalicious.domain.food.repository;

import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface PresetDailyFoodRepository extends JpaRepository<PresetDailyFood, BigInteger> {

}
