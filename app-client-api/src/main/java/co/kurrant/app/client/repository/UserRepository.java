package co.kurrant.app.client.repository;

import java.math.BigInteger;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.dalicious.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {
  Optional<User> findByEmail(String email);
}
