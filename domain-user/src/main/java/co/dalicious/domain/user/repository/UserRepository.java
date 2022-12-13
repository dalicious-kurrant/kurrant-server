package co.dalicious.domain.user.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.dalicious.domain.user.entity.User;

import javax.swing.text.html.Option;

@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {
  Optional<User> findByEmail(String email);
  Optional<User> findByPhone(String phone);
  Optional<User> findByNameAndEmail(String name, String email);
  List<User> findAll();

  boolean existsUserByEmail(String email);

}
