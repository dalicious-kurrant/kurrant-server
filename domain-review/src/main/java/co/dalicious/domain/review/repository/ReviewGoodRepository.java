package co.dalicious.domain.review.repository;

import co.dalicious.domain.review.entity.ReviewGood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ReviewGoodRepository extends JpaRepository<ReviewGood, BigInteger> {
}
