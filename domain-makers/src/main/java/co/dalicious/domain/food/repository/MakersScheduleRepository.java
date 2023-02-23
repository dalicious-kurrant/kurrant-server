package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.MakersSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MakersScheduleRepository extends JpaRepository<MakersSchedule, BigInteger> {
}