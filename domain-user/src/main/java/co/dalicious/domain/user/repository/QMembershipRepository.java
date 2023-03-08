package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
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
                        membership.endDate.gt(now),
                        membership.membershipStatus.eq(MembershipStatus.PROCESSING))
                .fetchCount();
        if(count > 1){
            throw new ApiException(ExceptionEnum.DUPLICATED_MEMBERSHIP);
        }
        return queryFactory
                .selectFrom(membership)
                .where(membership.user.eq(user),
                        membership.startDate.loe(now),
                        membership.endDate.goe(now),
                        membership.membershipStatus.eq(MembershipStatus.PROCESSING))
                .fetchOne();
    }

    public List<Membership> findAllByEndDate() {
        LocalDate now = LocalDate.now();
        return queryFactory.selectFrom(membership)
                .where(membership.endDate.eq(now),
                        membership.autoPayment.eq(true))
                .fetch();
    }
}
