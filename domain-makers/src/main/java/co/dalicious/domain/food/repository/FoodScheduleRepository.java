package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.FoodSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface FoodScheduleRepository extends JpaRepository<FoodSchedule, BigInteger> {
}