package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.CorporationApplicationForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorporationApplicationFormRepository extends JpaRepository<CorporationApplicationForm, Long> {
}
