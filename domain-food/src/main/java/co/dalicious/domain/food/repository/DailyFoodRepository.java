package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface DailyFoodRepository  extends JpaRepository<DailyFood, BigInteger> {

}
