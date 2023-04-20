package co.dalicious.data.redis.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;


@Getter
@RedisHash(value = "blackListTokenHash")
public class BlackListTokenHash {
    @Id
    String id;

    @Indexed
    String accessToken;

    @TimeToLive
    Long expiredIn;

    @Builder
    public BlackListTokenHash(String id, String accessToken, Long expiredIn) {
        this.id = id;
        this.accessToken = accessToken;
        this.expiredIn = expiredIn;
    }
}
