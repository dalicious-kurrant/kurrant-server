package co.dalicious.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;


public interface UserDepartmentRepository extends JpaRepository<UserDepartment, BigInteger> {

}
