package co.dalicious.data.redis.repository;

import co.dalicious.data.redis.entity.BlackListTokenHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface BlackListTokenRepository extends CrudRepository<BlackListTokenHash, String> {
    BlackListTokenHash findByAccessToken(String accessToken);
}
