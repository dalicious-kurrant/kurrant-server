package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, BigInteger> {
    Optional<Employee> findByCorporationAndEmail(Corporation corporation, String email);
    Optional<List<Employee>> findByEmail(String email);
}
