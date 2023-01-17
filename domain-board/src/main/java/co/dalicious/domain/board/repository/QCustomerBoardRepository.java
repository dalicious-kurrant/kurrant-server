package co.dalicious.domain.board.repository;

import co.dalicious.domain.board.entity.CustomerService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.dalicious.domain.board.entity.QCustomerService.customerService;

@Repository
@RequiredArgsConstructor
public class QCustomerBoardRepository {
    private final JPAQueryFactory queryFactory;


    public List<CustomerService> findAll() {
        return queryFactory
                .selectFrom(customerService)
                .fetch();
    }
}
