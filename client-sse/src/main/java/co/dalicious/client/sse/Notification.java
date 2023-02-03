package co.dalicious.client.sse;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;
import co.dalicious.domain.user.entity.User;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notification__notification")
public class Notification {

    @Id
    @Comment("ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;


    @Column
    @Comment("알림 타입")
    @Convert(converter = NotificationTypeConverter.class)
    private NotificationType type;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Comment("알림 읽음 여부")
    @Column(nullable = false)
    private boolean isRead;

    @Embedded
    @Comment("알림 내용")
    private NotificationContent content;

    Notification(NotificationType type, User user, boolean isRead, NotificationContent content) {
        this.type = type;
        this.user = user;
        this.isRead = isRead;
        this.content = content;
    }

    public void updateIsRead(boolean isRead) {
        this.isRead = isRead;
    }

}
