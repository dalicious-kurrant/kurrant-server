package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

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
}
