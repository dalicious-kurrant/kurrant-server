package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.ApartmentMealInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplyMealInfoRepository extends JpaRepository<ApartmentMealInfo, Long> {
}
