package co.dalicious.domain.user.repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import co.dalicious.domain.user.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import co.dalicious.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {
  Optional<User> findByEmail(String email);

  boolean existsUserByEmail(String email);

  List<User> findAll();

  Optional<User> findByPhone(String phone);

}
