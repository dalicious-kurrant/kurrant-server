package co.dalicious.domain.user.repository;

import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserApartment;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface UserApartmentRepository extends JpaRepository<UserApartment, BigInteger> {
    List<UserApartment> findAllByUser(User user);
    Optional<UserApartment> findOneByUserAndApartmentAndClientStatus(User user, Apartment apartment, ClientStatus clientStatus);
    List<UserApartment> findAllByUserAndClientStatus(User user, ClientStatus clientStatus);
}
