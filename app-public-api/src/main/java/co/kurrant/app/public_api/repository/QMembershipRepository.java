package co.kurrant.app.public_api.repository;

import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

import static co.dalicious.domain.user.entity.QMembership.membership;

@Repository
@RequiredArgsConstructor
public class QMembershipRepository {
    public final JPAQueryFactory queryFactory;

    public Membership findUserCurrentMembership(User user, LocalDate now) {
        long count = queryFactory
                .selectFrom(membership)
                .where(membership.user.eq(user),
                        membership.startDate.loe(now),
                        membership.endDate.gt(now))
                .fetchCount();
        if(count > 1){
            throw new IllegalStateException("Expected only one membership, but found :"+count);
        }
        Membership membership1 = queryFactory
                .selectFrom(membership)
                .where(membership.user.eq(user),
                        membership.startDate.loe(now),
                        membership.endDate.goe(now))
                .fetchOne();
        return membership1;
    }
}
