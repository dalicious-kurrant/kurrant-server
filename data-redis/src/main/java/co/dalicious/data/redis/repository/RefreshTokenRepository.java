package co.dalicious.data.redis.repository;

import co.dalicious.data.redis.entity.RefreshTokenHash;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenHash, String> {
    RefreshTokenHash findByUserId(String userId);
    void deleteByUserId(String userId);
}
