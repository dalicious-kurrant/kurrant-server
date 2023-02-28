package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.QMakers;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static co.dalicious.domain.food.entity.QMakers.makers;

@Repository
@RequiredArgsConstructor
public class QMakersRepository {

    private final JPAQueryFactory queryFactory;

    public Makers findOneByCode(String code) {
        return queryFactory.selectFrom(makers)
                .where(makers.code.eq(code))
                .fetchOne();
    }

    public List<Makers> getMakersByName(Set<String> makersName) {
        return queryFactory.selectFrom(makers)
                .where(makers.name.in(makersName))
                .fetch();
    }
}
