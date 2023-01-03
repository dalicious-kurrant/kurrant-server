package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.UserApartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UserApartmentRepository extends JpaRepository<UserApartment, BigInteger> {
}
