package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.Founders;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.Optional;

public interface FoundersRepository extends JpaRepository<Founders, BigInteger> {
    Optional<Founders> findOneByUserAndIsActive(User user, Boolean isActive);

    @Query("SELECT COALESCE(MAX(f.foundersNumber), 0) FROM Founders f")
    Integer getMaxFoundersNumber();
}
