package co.dalicious.domain.user.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import co.dalicious.domain.user.entity.enums.Role;
import co.dalicious.domain.user.entity.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import co.dalicious.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, BigInteger> {
  Optional<User> findOneByEmail(String email);
  Optional<User> findOneByPhone(String phone);
  Optional<User> findOneByRole(Role role);
  Optional<User> findOneByNameAndEmail(String name, String email);
  List<User> findAllByUserStatus(UserStatus userStatus);

  Boolean existsByRole(Role role);
  User findByName(String user1);
}
