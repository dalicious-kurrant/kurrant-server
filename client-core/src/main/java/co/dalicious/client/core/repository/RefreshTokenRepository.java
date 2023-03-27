package co.dalicious.client.core.repository;

import co.dalicious.client.core.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, BigInteger> {
    Optional<RefreshToken> findOneByRefreshToken(String refreshToken);
    List<RefreshToken> findAllByUserId(BigInteger userId);
}