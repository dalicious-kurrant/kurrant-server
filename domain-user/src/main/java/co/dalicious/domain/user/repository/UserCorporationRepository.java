package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.UserCorporation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;


public interface UserCorporationRepository extends JpaRepository<UserCorporation, BigInteger> {
}
