package co.dalicious.domain.user.entity;

import co.dalicious.domain.user.converter.PointConditionConverter;
import co.dalicious.domain.user.entity.enums.PointCondition;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

@Getter
@Entity
@Table(name = "user__point_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("포인트 로그 PK")
    private BigInteger id;

    @Convert(converter = PointConditionConverter.class)
    @Column(name = "e_point_condition")
    @Comment("포인트 적립 조건")
    private PointCondition pointCondition;

    @Column(name = "point")
    @Comment("적립 포인트")
    private BigDecimal point;

    @Column(name = "review_id")
    @Comment("리뷰 PK")
    private BigInteger reviewId;

    @Column(name = "order_id")
    @Comment("주문 PK")
    private BigInteger orderId;

    @Column(name = "board_id")
    @Comment("이벤트 공지 PK")
    private BigInteger boardId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "user_fk")
    @Comment("사용자 FK")
    private User user;

    @Column(name = "point_policy_id")
    @Comment("포인트 정책 PK")
    private BigInteger pointPolicyId;

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

    @Builder
    public PointHistory(BigInteger id, PointCondition pointCondition, BigDecimal point, BigInteger reviewId, BigInteger orderId, BigInteger boardId, User user, BigInteger pointPolicyId) {
        this.id = id;
        this.pointCondition = pointCondition;
        this.point = point;
        this.reviewId = reviewId;
        this.orderId = orderId;
        this.boardId = boardId;
        this.user = user;
        this.pointPolicyId = pointPolicyId;
    }
}