package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.client.entity.QEmployee;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.client.entity.QEmployee.employee;

@Repository
@RequiredArgsConstructor
public class QEmployeeRepository {

    private final JPAQueryFactory queryFactory;


    public Page<Employee> findAllByCorporationId(BigInteger corporationId, Pageable pageable) {
        QueryResults<Employee> results = queryFactory.selectFrom(employee)
                .where(employee.corporation.id.eq(corporationId))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
