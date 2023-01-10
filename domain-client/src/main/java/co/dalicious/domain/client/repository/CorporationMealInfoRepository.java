package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.CorporationMealInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorporationMealInfoRepository extends JpaRepository<CorporationMealInfo, Long> {
    List<CorporationMealInfo> findAllByGroup(Corporation corporation);
}
