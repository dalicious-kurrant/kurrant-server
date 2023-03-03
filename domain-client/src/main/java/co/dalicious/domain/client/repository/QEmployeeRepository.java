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

    public long deleteWaitingMember(BigInteger userId) {
        return queryFactory.delete(employee)
                .where(employee.id.eq(userId))
                .execute();
    }

    public void patchEmployee(BigInteger id, String phone, String email, String name) {
        queryFactory.update(employee)
                .set(employee.email, email)
                .set(employee.phone, phone)
                .set(employee.name, name)
                .where(employee.id.eq(id).or(employee.email.eq(email)))
                .execute();
    }
}
