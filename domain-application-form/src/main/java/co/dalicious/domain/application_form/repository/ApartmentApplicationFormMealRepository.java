package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.ApartmentApplicationMealInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApartmentApplicationFormMealRepository extends JpaRepository<ApartmentApplicationMealInfo, Long> {
}
