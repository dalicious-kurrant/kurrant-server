package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RecommendMakers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface RecommendMakersRepository extends JpaRepository<RecommendMakers, BigInteger> {
}
