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


    public List<Employee> findAllByCorporationId(BigInteger corporationId) {
        return queryFactory.selectFrom(employee)
                .where(employee.corporation.id.eq(corporationId))
                .fetch();
    }
}
