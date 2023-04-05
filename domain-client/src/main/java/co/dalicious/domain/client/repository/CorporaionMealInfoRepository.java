package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.CorporationMealInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface CorporaionMealInfoRepository extends JpaRepository<CorporationMealInfo, BigInteger> {

    List<CorporationMealInfo> findAllByGroupId(BigInteger id);
}
