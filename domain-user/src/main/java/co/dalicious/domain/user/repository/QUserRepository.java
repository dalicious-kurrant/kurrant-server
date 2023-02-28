package co.dalicious.domain.user.repository;


import co.dalicious.domain.user.entity.User;
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


    public User findByUserId(BigInteger userId) {
        return queryFactory.selectFrom(user)
                .where(user.id.eq(userId))
                .fetchOne();
    }

    public void updateUserInfo(User userEntity, String password) {

        //이름 변경
        if (!userEntity.getName().isEmpty()){
            queryFactory.update(user)
                    .set(user.name, userEntity.getName())
                    .where(user.id.eq(userEntity.getId()))
                    .execute();
        }
        //이메일 변경
        if (!userEntity.getEmail().isEmpty()){
            queryFactory.update(user)
                    .set(user.email, userEntity.getEmail())
                    .where(user.id.eq(userEntity.getId()))
                    .execute();
        }

        //비밀번호 변경
        if (!password.isEmpty()){
            queryFactory.update(user)
                    .set(user.password, password)
                    .where(user.id.eq(userEntity.getId()))
                    .execute();
        }

        //휴대폰 번호 변경
        if (!userEntity.getPhone().isEmpty()){
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
}
