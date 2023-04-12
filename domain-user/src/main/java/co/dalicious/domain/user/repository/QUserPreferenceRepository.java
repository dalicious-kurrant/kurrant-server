package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.UserPreference;
import co.dalicious.system.converter.FoodTagsConverter;
import co.dalicious.system.enums.FoodTag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static co.dalicious.domain.user.entity.QUserPreference.userPreference;

@Repository
@RequiredArgsConstructor
public class QUserPreferenceRepository {

    private final JPAQueryFactory queryFactory;

    public void deleteOthers(BigInteger userId) {
        queryFactory.delete(userPreference)
                .where(userPreference.user.id.eq(userId))
                .execute();
    }
}
