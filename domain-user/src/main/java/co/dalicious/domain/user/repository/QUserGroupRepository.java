package co.dalicious.domain.user.repository;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.PushCondition;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.*;

import static co.dalicious.domain.user.entity.QUser.user;
import static co.dalicious.domain.user.entity.QUserGroup.userGroup;

@Repository
@RequiredArgsConstructor
public class QUserGroupRepository {

    private final JPAQueryFactory queryFactory;


    public String findNameById(BigInteger corporationId) {
        return queryFactory.select(userGroup.group.name)
                .from(userGroup)
                .where(userGroup.id.eq(corporationId))
                .fetchOne();

    }

    public List<User> findAllByGroupId(BigInteger corporationId) {
        return queryFactory.select(userGroup.user)
                .from(userGroup)
                .where(userGroup.group.id.eq(corporationId),
                        userGroup.clientStatus.ne(ClientStatus.WITHDRAWAL))
                .fetch();
    }

    public List<UserGroup> findAllByUserIdAndGroupId(BigInteger userId, BigInteger groupId) {
        return queryFactory.selectFrom(userGroup)
                .where(userGroup.user.id.eq(userId),
                        userGroup.group.id.eq(groupId),
                        userGroup.clientStatus.eq(ClientStatus.BELONG))
                .fetch();
    }

    public Long deleteMember(BigInteger userId, BigInteger groupId) {
        return queryFactory.update(userGroup)
                .set(userGroup.clientStatus, ClientStatus.WITHDRAWAL)
                .where(userGroup.user.id.eq(userId),
                        userGroup.group.id.eq(groupId))
                .execute();
    }

    public Integer userCountInGroup(BigInteger groupId) {
        return queryFactory.selectFrom(userGroup)
                .where(userGroup.group.id.eq(groupId))
                .fetch().size();
    }

    public Map<Group, Integer> userCountsInGroup(List<Group> groups) {
        Map<Group, Integer> groupIntegerMap = new HashMap<>();
        List<Tuple> result = queryFactory.select(userGroup.group, userGroup.user.count())
                .from(userGroup)
                .where(userGroup.group.in(groups))
                .groupBy(userGroup.group)
                .fetch();

        for (Tuple tuple : result) {
            Group group = tuple.get(userGroup.group);
            Integer userCount = Objects.requireNonNull(tuple.get(userGroup.user.count())).intValue();
            groupIntegerMap.put(group, userCount);
        }

        return groupIntegerMap;
    }

    public Map<User, Group> findUserGroupFirebaseToken(Set<BigInteger> groupIds) {
        List<Tuple> results = queryFactory.select(user, userGroup.group)
                .from(userGroup)
                .leftJoin(userGroup.user, user)
                .where(userGroup.group.id.in(groupIds), user.firebaseToken.isNotNull())
                .fetch();

        Map<User, Group> userGroupMap = new HashMap<>();

        for (Tuple result : results) {
            User user = result.get(0, User.class);
            Group group = result.get(1, Group.class);
            userGroupMap.put(user, group);
        }

        return userGroupMap;
    }

    public Map<User, Group> findUserGroupFirebaseTokenByGroup(Set<Group> groups) {
        List<Tuple> results = queryFactory.select(user, userGroup.group)
                .from(userGroup)
                .leftJoin(userGroup.user, user)
                .where(userGroup.group.in(groups), user.firebaseToken.isNotNull())
                .fetch();

        Map<User, Group> userGroupMap = new HashMap<>();

        for (Tuple result : results) {
            User user = result.get(0, User.class);
            Group group = result.get(1, Group.class);
            userGroupMap.put(user, group);
        }

        return userGroupMap;
    }

    public List<User> findAllUserByGroupIds(List<? extends Group> groups) {
        return queryFactory.select(user)
                .from(userGroup)
                .where(userGroup.group.in(groups))
                .fetch();
    }
}
