package co.dalicious.domain.user.repository;


import co.dalicious.domain.user.entity.enums.UserStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static co.dalicious.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QUserRepository {

    private final JPAQueryFactory queryFactory;
    public Long deleteMember(BigInteger userId) {
        return queryFactory.update(user)
                .set(user.userStatus, UserStatus.INACTIVE)
                .where(user.id.eq(userId))
                .execute();
    }
}
