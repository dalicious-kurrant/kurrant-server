package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface MealInfoRepository extends JpaRepository<MealInfo, BigInteger> {

    List<MealInfo> findAllBySpotId(BigInteger spotId);
}
