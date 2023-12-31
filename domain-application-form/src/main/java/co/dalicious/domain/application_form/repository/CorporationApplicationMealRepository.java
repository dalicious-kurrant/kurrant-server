package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.CorporationApplicationForm;
import co.dalicious.domain.application_form.entity.CorporationApplicationMealInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface CorporationApplicationMealRepository extends JpaRepository<CorporationApplicationMealInfo, Long> {
    List<CorporationApplicationMealInfo> findAllByCorporationApplicationForm(@NotNull CorporationApplicationForm corporationApplicationForm);
}
