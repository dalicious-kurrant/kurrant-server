package co.dalicious.data.redis.repository;

import co.dalicious.data.redis.entity.RefreshTokenHash;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenHash, String> {
    List<RefreshTokenHash> findAllByUserId(String userId);
    Optional<RefreshTokenHash> findOneByRefreshToken(String refreshToken);
}
