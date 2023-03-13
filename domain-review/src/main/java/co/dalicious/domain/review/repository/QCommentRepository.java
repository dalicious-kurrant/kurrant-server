package co.dalicious.domain.review.repository;

import co.dalicious.domain.review.entity.Comments;
import co.dalicious.domain.review.entity.Reviews;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static co.dalicious.domain.review.entity.QComments.comments;

@Repository
@RequiredArgsConstructor
public class QCommentRepository {
    public final JPAQueryFactory queryFactory;

    public Map<Reviews, List<Comments>> findAllByReviews(Set<Reviews> reviewsList) {
        Map<Reviews, List<Comments>> map = new HashMap<>();

        List<Comments> commentsList = queryFactory.selectFrom(comments)
                .where(comments.reviews.in(reviewsList))
                .fetch();

        reviewsList.forEach(reviews -> {
            List<Comments> value = commentsList.stream().filter(v -> v.getReviews().equals(reviews)).toList();
            map.put(reviews, value);
        });

        return map;
    }
}
