package co.dalicious.client.sse;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class NotificationContent {

    private int MAX_LENGTH = 50;

    @Column(name = "notification_content")
    @Comment("알림 내용")
    private String content;

    public NotificationContent(String content){
        if(content.isEmpty() || content == null) {
            this.content = null;
        } else if(content.length() > MAX_LENGTH) {
            this.content = content.substring(0, 50);
        } else {
            this.content = content;
        }
    }
}
