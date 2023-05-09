package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.Founders;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.user.entity.QFounders.founders;

@Repository
@RequiredArgsConstructor
public class QFoundersRepository {
    private final JPAQueryFactory queryFactory;

    public List<Founders> findAllFoundersByUserList(List<User> userList) {
        return queryFactory.selectFrom(founders)
                .where(founders.user.in(userList))
                .fetch();
    }
}
