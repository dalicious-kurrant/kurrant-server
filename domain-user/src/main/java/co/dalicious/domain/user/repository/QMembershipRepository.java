package co.dalicious.domain.user.repository;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.system.util.PeriodDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static co.dalicious.domain.user.entity.QMembership.membership;
import static co.dalicious.domain.user.entity.QUserGroup.userGroup;

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

    public List<Membership> findAllByFilter(LocalDate startDate, LocalDate endDate, Group group, BigInteger userId) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if(startDate != null) {
            whereClause.and(membership.startDate.loe(startDate));
        }
        if(endDate != null) {
            whereClause.and(membership.endDate.goe(endDate));
        }
        if(userId != null) {
            whereClause.and(membership.user.id.eq(userId));
        }
        if(userId == null && group != null) {
            List<User> groupUsers = queryFactory.selectFrom(userGroup.user)
                    .where(userGroup.group.eq(group), userGroup.clientStatus.eq(ClientStatus.BELONG))
                    .fetch();

            whereClause.and(membership.user.in(groupUsers));
        }
        return queryFactory
                .selectFrom(membership)
                .where(membership.membershipStatus.eq(MembershipStatus.PROCESSING),
                        whereClause)
                .fetch();
    }
}
