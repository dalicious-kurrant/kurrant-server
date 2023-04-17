package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.MakersCapacity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface MakersCapacityRepository extends JpaRepository<MakersCapacity, BigInteger> {

    List<MakersCapacity> findByMakersId(BigInteger makersId);
}
