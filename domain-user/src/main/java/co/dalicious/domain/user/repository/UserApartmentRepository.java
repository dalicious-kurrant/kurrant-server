package co.dalicious.domain.user.repository;

import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.user.entity.ClientStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserApartment;
import co.dalicious.domain.user.entity.UserCorporation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface UserApartmentRepository extends JpaRepository<UserApartment, BigInteger> {
    List<UserApartment> findByUser(User user);
    Optional<UserApartment> findByUserAndApartmentAndClientStatus(User user, Apartment apartment, ClientStatus clientStatus);
    List<UserApartment> findByUserAndClientStatus(User user, ClientStatus clientStatus);
}
