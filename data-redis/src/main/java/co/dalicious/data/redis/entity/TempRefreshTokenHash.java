package co.dalicious.data.redis.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@RedisHash(value = "tempRefreshTokenHash", timeToLive = 10L)
public class TempRefreshTokenHash {
    @Id
    String id;

    @Indexed
    String userId;

    String oldRefreshToken;

    String newRefreshToken;

    String newAccessToken;

    @Builder
    public TempRefreshTokenHash(String id, String userId, String oldRefreshToken, String newRefreshToken, String newAccessToken) {
        this.id = id;
        this.userId = userId;
        this.oldRefreshToken = oldRefreshToken;
        this.newRefreshToken = newRefreshToken;
        this.newAccessToken = newAccessToken;
    }
}
