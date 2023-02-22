package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.CorporationMealInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;


public interface CorporationMealInfoRepository extends JpaRepository<CorporationMealInfo, Long> {
    CorporationMealInfo findBySpotId(BigInteger spotId);
}
