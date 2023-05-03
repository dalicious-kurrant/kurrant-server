package co.dalicious.domain.review.repository;

import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Reviews, BigInteger> {

    public List<Reviews> findByUser(User user);

    Reviews findByFoodId(BigInteger id);

    List<Reviews> findAllByFoodId(BigInteger id);
}
