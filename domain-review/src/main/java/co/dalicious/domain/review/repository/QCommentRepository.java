package co.dalicious.domain.review.repository;

import co.dalicious.domain.review.entity.Comments;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QCommentRepository {
    public final JPAQueryFactory queryFactory;

    public List<Comments> findAllByReviews
}
