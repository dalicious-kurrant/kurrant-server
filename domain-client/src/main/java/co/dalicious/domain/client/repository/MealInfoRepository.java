package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.MealInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MealInfoRepository extends JpaRepository<MealInfo, BigInteger> {
    MealInfo findByGroupId(BigInteger id);
}
