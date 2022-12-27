package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.ApplicationFormApartment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationFormApartmentRepository extends JpaRepository<ApplicationFormApartment, Long> {
}
