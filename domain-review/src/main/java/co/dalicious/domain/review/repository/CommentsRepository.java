package co.dalicious.domain.review.repository;

import co.dalicious.domain.review.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, BigInteger> {

    List<Comments> findAllByReviewsId(BigInteger reviewsId);
}
