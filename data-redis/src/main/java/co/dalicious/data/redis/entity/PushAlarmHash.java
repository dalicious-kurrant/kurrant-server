package co.dalicious.data.redis.entity;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;
import java.math.BigInteger;


@Getter
@RedisHash(value = "pushAlarmHash", timeToLive = 60 * 60 * 24 * 30 )
public class PushAlarmHash {
    @Id
    String id;

    String title;

    String message;

    @Indexed
    BigInteger userId;

    String redirectUrl;
    String createdDateTime;
    BigInteger reviewId;

    @Builder

    public PushAlarmHash(String title, String message, BigInteger userId, String redirectUrl, String createdDateTime, BigInteger reviewId) {
        this.title = title;
        this.message = message;
        this.userId = userId;
        this.redirectUrl = redirectUrl;
        this.createdDateTime = createdDateTime;
        this.reviewId = reviewId;
    }
}
