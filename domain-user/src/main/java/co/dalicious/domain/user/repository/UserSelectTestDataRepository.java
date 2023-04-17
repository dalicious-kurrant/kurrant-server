package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.UserSelectTestData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UserSelectTestDataRepository extends JpaRepository<UserSelectTestData, BigInteger> {

}
