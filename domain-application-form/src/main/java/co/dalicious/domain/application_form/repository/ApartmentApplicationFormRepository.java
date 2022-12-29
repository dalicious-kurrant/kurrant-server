package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.ApartmentApplicationForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface ApartmentApplicationFormRepository extends JpaRepository<ApartmentApplicationForm, Long> {
    List<ApartmentApplicationForm> findByUserId(BigInteger userId);
}
