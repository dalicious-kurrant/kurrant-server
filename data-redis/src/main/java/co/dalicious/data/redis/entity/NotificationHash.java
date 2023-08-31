package co.dalicious.data.redis.entity;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;
import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@RedisHash(value = "notificationHash", timeToLive = 30 * 24 * 60 * 60) // 만료시간 30일
public class NotificationHash {
    @Id
    String id;

    // 1: 전체공지, 2: 스팟공지, 3: 구매후기, 4: 마감시간, 5: 다음주 주문, 6: 푸시알림, 7: 그룹, 8: 댓글
    @Indexed
    Integer type;

    @Indexed
    BigInteger userId;

    @Indexed
    boolean isRead;

    String content;

    LocalDate createDate;
    BigInteger groupId;
    BigInteger commentId;
    BigInteger noticeId;

    @Builder
    public NotificationHash(String id, Integer type, BigInteger userId, boolean isRead, String content, LocalDate createDate, BigInteger groupId, BigInteger commentId, BigInteger noticeId) {
        this.id = id;
        this.type = type;
        this.userId = userId;
        this.isRead = isRead;
        checkNotificationContent(content);
        this.createDate = createDate;
        this.groupId = groupId;
        this.commentId = commentId;
        this.noticeId = noticeId;
    }

    private void checkNotificationContent(String content){
        if(content == null || content.isEmpty()) {
            this.content = null;
        } else if(content.length() > 50) {
            this.content = content.substring(0, 50);
        } else {
            this.content = content;
        }
    }

    public void updateRead(boolean isRead) {
        this.isRead = isRead;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public void setCommentId(BigInteger commentId) {
        this.commentId = commentId;
    }
}
