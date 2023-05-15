package co.dalicious.domain.user.entity;

import co.dalicious.domain.user.converter.PushConditionConverter;
import co.dalicious.domain.user.entity.enums.PushCondition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user__review_deadline_push_alarm_log")
public class BatchPushAlarmLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("리뷰 마감 시간 알림 로그 PK")
    private BigInteger id;

    @Convert(converter = PushConditionConverter.class)
    @Column(name = "push_condition")
    @Comment("알림 조건")
    private PushCondition pushCondition;

    @Column(name = "user_id")
    @Comment("유저 Id")
    private BigInteger userId;

    @Column(name = "push_date_time")
    @Comment("푸시알림 전송 시간")
    private LocalDateTime pushDateTime;

    @Builder
    public BatchPushAlarmLog(BigInteger userId, LocalDateTime pushDateTime, PushCondition pushCondition) {
        this.userId = userId;
        this.pushDateTime = pushDateTime;
        this.pushCondition = pushCondition;
    }

    public void updatePushDateTime(LocalDateTime pushDateTime) {
        this.pushDateTime = pushDateTime;
    }
}
