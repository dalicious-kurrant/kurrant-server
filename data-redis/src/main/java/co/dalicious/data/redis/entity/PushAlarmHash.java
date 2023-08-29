package co.dalicious.data.redis.entity;


import lombok.Builder;
import lombok.Getter;
import org.mapstruct.ap.shaded.freemarker.template.utility.DateUtil;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;
import java.math.BigInteger;
import java.time.LocalDateTime;


@Getter
@RedisHash(value = "pushAlarmHash", timeToLive = 60 * 60 * 24 * 30 )
public class PushAlarmHash {
    @Id
    String id;

    String title;

    String message;

    @Indexed
    BigInteger userId;

    String type;
    LocalDateTime createdDateTime;
    Boolean isRead;
    BigInteger reviewId;
    BigInteger noticeId;

    @Builder
    public PushAlarmHash(String title, String message, BigInteger userId, String type, Boolean isRead, BigInteger reviewId, BigInteger noticeId) {
        this.title = title;
        this.message = message;
        this.userId = userId;
        this.type = type;
        this.createdDateTime = LocalDateTime.now();
        this.isRead = isRead;
        this.reviewId = reviewId;
        this.noticeId = noticeId;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
