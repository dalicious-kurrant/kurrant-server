package co.dalicious.domain.user.repository;

import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UserSpotRepository extends JpaRepository<UserSpot, BigInteger> {
}
