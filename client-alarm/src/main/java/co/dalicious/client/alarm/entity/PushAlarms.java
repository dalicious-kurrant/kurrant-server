package co.dalicious.client.alarm.entity;

import co.dalicious.client.alarm.converter.PushStatusConverter;
import co.dalicious.domain.user.converter.PushConditionConverter;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.client.alarm.entity.enums.PushStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "alarm__push_alarm")
public class PushAlarms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("푸시알람 PK")
    private BigInteger id;

    @Convert(converter = PushStatusConverter.class)
    @Column(name = "e_push_status")
    @Comment("푸시알림 상태 - 0. 활성 / 1. 비활성")
    private PushStatus pushStatus;

    @Convert(converter = PushConditionConverter.class)
    @Column(name = "push_condition")
    @Comment("푸시알림 조건")
    private PushCondition condition;

    @Column(name = "message")
    @Comment("메시지")
    private String message;

    @Column(name = "redirect_url")
    @Comment("이동할 주소")
    private String redirectUrl;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_date_time", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "updated_date_time",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @Builder
    public PushAlarms(BigInteger id, PushStatus pushStatus, PushCondition condition, String message, String redirectUrl) {
        this.id = id;
        this.pushStatus = pushStatus;
        this.condition = condition;
        this.message = message;
        this.redirectUrl = redirectUrl;
    }

    public void updateMessage(String message) {
        this.message = message;
    }

    public void updatePushStatus(PushStatus pushStatus) {
        this.pushStatus = pushStatus;
    }

    public void updateRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
}
