package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.MySpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MySpotRepository extends JpaRepository<MySpot, BigInteger> {
}
