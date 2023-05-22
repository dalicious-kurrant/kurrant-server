package co.dalicious.domain.review.repository;

import co.dalicious.domain.review.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, BigInteger> {
    void deleteAllByFoodId(BigInteger foodId);

}
