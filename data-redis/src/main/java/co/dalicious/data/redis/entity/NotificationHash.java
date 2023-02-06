package co.dalicious.data.redis.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@RedisHash(value = "notificationHash")
public class NotificationHash {
    @Id
    String id;

    @Indexed
    Integer type;

    @Indexed
    BigInteger userId;

    @Indexed
    boolean isRead;

    String content;

    @Builder
    public NotificationHash(String id, Integer type, BigInteger userId, boolean isRead, String content) {
        this.id = id;
        this.type = type;
        this.userId = userId;
        this.isRead = isRead;
        checkNotificationContent(content);
    }

    private void checkNotificationContent(String content){
        if(content.isEmpty() || content == null) {
            this.content = null;
        } else if(content.length() > 50) {
            this.content = content.substring(0, 50);
        } else {
            this.content = content;
        }
    }
}
