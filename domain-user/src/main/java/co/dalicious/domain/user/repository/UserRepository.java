package co.dalicious.domain.user.repository;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import co.dalicious.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, BigInteger> {
  Optional<User> findOneByEmail(String email);
  Optional<User> findOneByPhone(String phone);
  Optional<User> findOneByNameAndEmail(String name, String email);
}
