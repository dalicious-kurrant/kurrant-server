package co.dalicious.data.redis.repository;

import co.dalicious.data.redis.entity.RefreshTokenHash;
import co.dalicious.data.redis.entity.TempRefreshTokenHash;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TempRefreshTokenRepository extends CrudRepository<TempRefreshTokenHash, String> {
    Optional<TempRefreshTokenHash> findOneByRefreshToken(String refreshToken);
}
