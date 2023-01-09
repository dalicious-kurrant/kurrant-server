package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.CorporationApplicationForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface CorporationApplicationFormRepository extends JpaRepository<CorporationApplicationForm, Long> {
    List<CorporationApplicationForm> findAllByUserId(BigInteger id);
}
