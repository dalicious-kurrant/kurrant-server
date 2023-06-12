package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;


public interface DepartmentRepository extends JpaRepository<Department, BigInteger> {
    Department findByName(String name);
}
