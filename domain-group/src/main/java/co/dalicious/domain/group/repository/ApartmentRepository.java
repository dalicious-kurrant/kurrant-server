package co.dalicious.domain.group.repository;

import co.dalicious.domain.group.entity.ClientApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepository extends JpaRepository<ClientApartment, Long> {
}
