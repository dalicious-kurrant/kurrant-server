package co.dalicious.domain.recommend.repository;

import co.dalicious.domain.recommend.entity.FoodRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface FoodRecommendRepository extends JpaRepository<FoodRecommend, BigInteger> {
}