package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, BigInteger> {


    List<UserPreference> findByUserId(BigInteger userId);
}
