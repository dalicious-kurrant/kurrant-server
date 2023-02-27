package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.QMakers;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
