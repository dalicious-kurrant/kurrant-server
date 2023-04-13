package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.enums.DiningType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface SpotRepository extends JpaRepository<Spot, BigInteger> {

}
