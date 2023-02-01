package co.dalicious.client.sse;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;
import co.dalicious.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notification__notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Convert(converter = NotificationTypeConverter.class)
    @Column
    private NotificationType type;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private boolean isRead;

    @Column
    private String content;

    Notification(NotificationType type, User user, boolean isRead) {
        this.type = type;
        this.user = user;
        this.isRead = isRead;
    }

    Notification(NotificationType type, User user, boolean isRead, String content) {
        this.type = type;
        this.user = user;
        this.isRead = isRead;
        this.content = content;
    }

    public void updateIsRead(boolean isRead) {
        this.isRead = isRead;
    }

}
