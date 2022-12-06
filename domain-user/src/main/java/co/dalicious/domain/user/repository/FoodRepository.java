package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface FoodRepository extends JpaRepository<Food, BigInteger> {


    Food findById(Integer foodId);
}