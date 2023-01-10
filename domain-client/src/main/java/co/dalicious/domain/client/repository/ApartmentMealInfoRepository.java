package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.ApartmentMealInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApartmentMealInfoRepository extends JpaRepository<ApartmentMealInfo, Long> {
    List<ApartmentMealInfo> findAllByGroup(Apartment apartment);
}
