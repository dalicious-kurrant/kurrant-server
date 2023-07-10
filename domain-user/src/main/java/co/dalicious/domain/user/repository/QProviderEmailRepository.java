package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.user.entity.QProviderEmail.providerEmail;

@Repository
@RequiredArgsConstructor
public class QProviderEmailRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<ProviderEmail> getProviderEmails(List<String> emails) {
        return jpaQueryFactory.selectFrom(providerEmail)
                .where(providerEmail.email.in(emails))
                .fetch();
    }

    public List<ProviderEmail> findAllByUsers(List<User> userList) {
        return jpaQueryFactory.selectFrom(providerEmail)
                .where(providerEmail.user.in(userList))
                .fetch();
    }
}
