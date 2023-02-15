package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, BigInteger> {
    Optional<Food> findOneById(BigInteger foodId);
    List<Food> findByMakersOrderById(Makers makers);
    Food findByIdAndMakers(BigInteger foodId, Makers makers);
}