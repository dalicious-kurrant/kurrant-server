package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, BigInteger> {
    List<Employee> findAllByEmail(String email);
    List<Employee> findAllByCorporation(Corporation corporation);

    void deleteAllByCorporationId(BigInteger corporationId);

    void deleteAllByEmail(String email);
}
