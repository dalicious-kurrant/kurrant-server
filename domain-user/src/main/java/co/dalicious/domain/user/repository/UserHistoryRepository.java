package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UserHistoryRepository extends JpaRepository<UserHistory, BigInteger> {


}
