package co.dalicious.data.redis.repository;

import co.dalicious.data.redis.entity.RefreshTokenHash;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenHash, String> {
    List<RefreshTokenHash> findAllByUserId(String userId);
}
