package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.CorporationApplicationForm;
import co.dalicious.domain.application_form.entity.CorporationApplicationMealInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface CorporationMealInfoRepository extends JpaRepository<CorporationApplicationMealInfo, Long> {
    List<CorporationApplicationMealInfo> findByCorporationApplicationForm(@NotNull CorporationApplicationForm corporationApplicationForm);
}
