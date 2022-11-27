package co.dalicious.domain.group.repository;

import co.dalicious.domain.group.entity.ClientCorporation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoporationRepository extends JpaRepository<ClientCorporation, Long> {
}
