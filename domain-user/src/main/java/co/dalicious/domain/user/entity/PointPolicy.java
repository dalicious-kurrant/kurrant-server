package co.dalicious.domain.user.entity;

import co.dalicious.domain.user.converter.PointConditionConverter;
import co.dalicious.domain.user.dto.PointPolicyReqDto;
import co.dalicious.domain.user.entity.enums.PointCondition;
import co.dalicious.system.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;


@Getter
@Entity
@Table(name = "user__point_policy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("포인트 정책 PK")
    private BigInteger id;

    @Convert(converter = PointConditionConverter.class)
    @Column(name = "e_point_condition")
    @Comment("포인트 적립 조건")
    private PointCondition pointCondition;

    @Column(name = "completed_condition_count")
    @Comment("조건 완료 횟수")
    private Integer completedConditionCount;

    @Column(name = "account_completion_limit")
    @Comment("계정 당 횟수")
    private Integer accountCompletionLimit;

    @Column(name = "reward_point")
    @Comment("보상 포인트")
    private BigDecimal rewardPoint;

    @Column(name = "event_start_date")
    @Comment("시작일")
    private LocalDate eventStartDate;

    @Column(name = "event_end_date")
    @Comment("종료일")
    private LocalDate eventEndDate;


    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "updated_datetime",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @Column(name = "board_id")
    @Comment("이벤트 공지 PK")
    private BigInteger boardId;


    @Builder
    public PointPolicy(BigInteger id, PointCondition pointCondition, Integer completedConditionCount, Integer accountCompletionLimit, BigDecimal rewardPoint, LocalDate eventStartDate, LocalDate eventEndDate, BigInteger boardId) {
        this.id = id;
        this.pointCondition = pointCondition;
        this.completedConditionCount = completedConditionCount;
        this.accountCompletionLimit = accountCompletionLimit;
        this.rewardPoint = rewardPoint;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.boardId = boardId;
    }

    public void updatePointPolicy(PointPolicyReqDto.EventPointPolicy eventPointPolicy) {
        this.pointCondition = PointCondition.ofCode(eventPointPolicy.getPointCondition());
        this.completedConditionCount = eventPointPolicy.getCompletedConditionCount();
        this.accountCompletionLimit = eventPointPolicy.getAccountCompletionLimit();
        this.rewardPoint = BigDecimal.valueOf(eventPointPolicy.getRewardPoint());
        this.eventStartDate = eventPointPolicy.getEventStartDate() == null ? null : DateUtils.stringToDate(eventPointPolicy.getEventStartDate());
        this.eventEndDate = eventPointPolicy.getEventEndDate() == null ? null : DateUtils.stringToDate(eventPointPolicy.getEventEndDate());
    }

    public void updateBoardId(BigInteger boardId) {
        this.boardId = boardId;
    }
}
