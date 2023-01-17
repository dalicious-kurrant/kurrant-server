package co.dalicious.domain.board.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static co.dalicious.domain.board.entity.QCustomerService.customerService;

@Repository
@RequiredArgsConstructor
public class QCustomerBoardRepository {
    private final JPAQueryFactory queryFactory;


    public Object findAll() {
        return queryFactory.selectFrom(customerService)
                .fetchAll();
    }
}
