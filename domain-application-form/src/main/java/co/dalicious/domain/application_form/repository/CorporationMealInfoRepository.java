package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.CorporationApplicationForm;
import co.dalicious.domain.application_form.entity.CorporationMealInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface CorporationMealInfoRepository extends JpaRepository<CorporationMealInfo, Long> {
    List<CorporationMealInfo> findByCorporationApplicationForm(@NotNull CorporationApplicationForm corporationApplicationForm);
}
