package co.dalicious.domain.user.repository;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<String> findUserGroupFirebaseToken(List<BigInteger> groupIds) {
        return queryFactory.select(user.firebaseToken)
                .from(userGroup)
                .leftJoin(userGroup.user, user)
                .where(userGroup.group.id.in(groupIds))
                .fetch();
    }
}
