package co.dalicious.integration.client.user.reposiitory;

import co.dalicious.integration.client.user.entity.MySpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MySpotRepository extends JpaRepository<MySpot, BigInteger> {
}
