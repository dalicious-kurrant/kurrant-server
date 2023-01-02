package co.dalicious.domain.user.repository;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import co.dalicious.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, BigInteger> {
  Optional<User> findByEmail(String email);
  Optional<User> findByPhone(String phone);
  Optional<User> findByNameAndEmail(String name, String email);
}
