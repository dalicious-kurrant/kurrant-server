package co.dalicious.domain.user.repository;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserCorporation;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface UserCorporationRepository extends JpaRepository<UserCorporation, BigInteger> {
    Optional<UserCorporation> findByUserAndCorporationAndClientStatus(User user, Corporation corporation, ClientStatus clientStatus);
    List<UserCorporation> findByUser(User user);
    List<UserCorporation> findByUserAndClientStatus(User user, ClientStatus clientStatus);
}
