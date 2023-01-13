package co.dalicious.data.redis.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@RedisHash(value = "refreshTokenHash", timeToLive = 7 * 2 * 24 * 60 * 60 * 1000L)
public class RefreshTokenHash {
    @Id
    String id;

    @Indexed
    String userId;

    @Indexed
    String refreshToken;

    @Builder
    public RefreshTokenHash(String id, String userId, String refreshToken) {
        this.id = id;
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
