package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.UserDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;


public interface UserDepartmentRepository extends JpaRepository<UserDepartment, BigInteger> {

}
