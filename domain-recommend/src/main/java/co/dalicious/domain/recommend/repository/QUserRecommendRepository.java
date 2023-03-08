package co.dalicious.domain.recommend.repository;

import co.dalicious.domain.recommend.dto.UserRecommendWhereData;
import co.dalicious.domain.recommend.entity.UserRecommends;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.recommend.entity.QUserRecommends.userRecommends;

@Repository
@RequiredArgsConstructor
public class QUserRecommendRepository {

    private final JPAQueryFactory queryFactory;

    public List<UserRecommends> getUserRecommends(UserRecommendWhereData data) {
        return queryFactory.selectFrom(userRecommends)
                .where(userRecommends.userId.eq(data.getUserId()),
                        userRecommends.serviceDate.eq(data.getServiceDate()),
                        userRecommends.foodId.in(data.getFoodId()))
                .fetch();
    }
}
