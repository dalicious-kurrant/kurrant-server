package co.dalicious.domain.review.repository;

import co.dalicious.domain.review.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ReviewRepository extends JpaRepository<Reviews, BigInteger> {
}
