package co.dalicious.domain.user.repository;


import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.UserStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static co.dalicious.domain.client.entity.QGroup.group;
import static co.dalicious.domain.user.entity.QUser.user;
import static co.dalicious.domain.user.entity.QUserGroup.userGroup;

@Repository
@RequiredArgsConstructor
public class QUserRepository {

    private final JPAQueryFactory queryFactory;


    public User findByUserId(BigInteger userId) {
        return queryFactory.selectFrom(user)
                .where(user.id.eq(userId))
                .fetchOne();
    }

    public void updateUserInfo(User userEntity, String password) {

        //이름 변경
        if (!userEntity.getName().isEmpty()) {
            queryFactory.update(user)
                    .set(user.name, userEntity.getName())
                    .where(user.id.eq(userEntity.getId()))
                    .execute();
        }
        //이메일 변경
        if (!userEntity.getEmail().isEmpty()) {
            queryFactory.update(user)
                    .set(user.email, userEntity.getEmail())
                    .where(user.id.eq(userEntity.getId()))
                    .execute();
        }

        //비밀번호 변경
        if (!password.isEmpty()) {
            queryFactory.update(user)
                    .set(user.password, password)
                    .where(user.id.eq(userEntity.getId()))
                    .execute();
        }

        //휴대폰 번호 변경
        if (!userEntity.getPhone().isEmpty()) {
            queryFactory.update(user)
                    .set(user.phone, userEntity.getPhone())
                    .where(user.id.eq(userEntity.getId()))
                    .execute();
        }


    }

    public void resetPassword(BigInteger userId, String password) {
        queryFactory.update(user)
                .set(user.password, password)
                .where(user.id.eq(userId))
                .execute();
    }

    public long deleteReal(User deleteUser) {
        return queryFactory.delete(user)
                .where(user.id.eq(deleteUser.getId()))
                .execute();
    }

    public List<User> findAllByParameter(Map<String, Object> parameters) {
        Integer userStatus = !parameters.containsKey("userStatus") || parameters.get("userStatus").equals("") ? null : Integer.parseInt((String) parameters.get("userStatus"));
        BigInteger groupId = !parameters.containsKey("group") || parameters.get("group").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("group")));
        BigInteger userId = !parameters.containsKey("userId") || parameters.get("userId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("userId")));

        BooleanBuilder whereClause = new BooleanBuilder();

        if (userStatus != null) {
            whereClause.and(user.userStatus.eq(UserStatus.ofCode(userStatus)));
        }
        if (groupId != null) {
            whereClause.and(
                    JPAExpressions.selectFrom(userGroup)
                            .join(userGroup.group, group)
                            .where(userGroup.user.eq(user), group.id.eq(groupId))
                            .exists()
            );
        }
        if (userId != null) {
            whereClause.and(user.id.eq(userId));
        }

        return queryFactory.selectFrom(user)
                .where(whereClause)
                .fetch();
    }
}
