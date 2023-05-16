package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.MySpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MySpotRepository extends JpaRepository<MySpot, BigInteger> {
}
