package co.dalicious.domain.user.entity;

import co.dalicious.domain.user.converter.PointStatusConverter;
import co.dalicious.domain.user.entity.enums.PointStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

@Getter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user__point_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("포인트 로그 PK")
    private BigInteger id;

    @Convert(converter = PointStatusConverter.class)
    @Column(name = "e_point_status")
    @Comment("포인트 상태")
    private PointStatus pointStatus;

    @Column(name = "point")
    @Comment("적립 포인트")
    private BigDecimal point;

    @Column(name = "review_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("리뷰 PK")
    private BigInteger reviewId;

    @Column(name = "order_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("주문 PK")
    private BigInteger orderId;

    @Column(name = "board_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("이벤트 공지 PK")
    private BigInteger boardId;

    @Column(name = "payment_cancel_history_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("환불내역 PK")
    private BigInteger paymentCancelHistoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "user_fk")
    @Comment("사용자 FK")
    private User user;

    @Column(name = "left_point")
    @Comment("잔액 포인트")
    private BigDecimal leftPoint;

    @Column(name = "point_policy_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("포인트 정책 PK")
    private BigInteger pointPolicyId;

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
    public PointHistory(BigInteger id, PointStatus pointStatus, BigDecimal point, BigInteger reviewId, BigInteger orderId, BigInteger boardId, BigInteger paymentCancelHistoryId, User user, BigDecimal leftPoint, BigInteger pointPolicyId) {
        this.id = id;
        this.pointStatus = pointStatus;
        this.point = point;
        this.reviewId = reviewId;
        this.orderId = orderId;
        this.boardId = boardId;
        this.paymentCancelHistoryId = paymentCancelHistoryId;
        this.user = user;
        this.leftPoint = leftPoint;
        this.pointPolicyId = pointPolicyId;
    }
}
