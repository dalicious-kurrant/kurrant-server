package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.CorporationApplicationForm;
import co.dalicious.domain.application_form.entity.CorporationApplicationFormSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface CorporationApplicationFormSpotRepository extends JpaRepository<CorporationApplicationFormSpot, Long> {
    List<CorporationApplicationFormSpot> findByCorporationApplicationForm(@NotNull CorporationApplicationForm corporationApplicationForm);
}
