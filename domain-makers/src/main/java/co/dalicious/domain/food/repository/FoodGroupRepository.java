package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.FoodGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface FoodGroupRepository extends JpaRepository<FoodGroup, BigInteger> {
}