package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.ApartmentApplicationForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationFormApartmentRepository extends JpaRepository<ApartmentApplicationForm, Long> {
}
