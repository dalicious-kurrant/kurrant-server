package co.dalicious.data.redis.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@RedisHash(value = "tempRefreshTokenHash", timeToLive = 10 * 1000L)
public class TempRefreshTokenHash {
    @Id
    String id;

    @Indexed
    String refreshToken;

    @Builder
    public TempRefreshTokenHash(String id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
    }
}
