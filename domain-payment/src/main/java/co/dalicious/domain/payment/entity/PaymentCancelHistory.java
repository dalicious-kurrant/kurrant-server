package co.dalicious.domain.payment.entity;

import co.dalicious.domain.order.entity.OrderItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "payment__cancel_history")
public class PaymentCancelHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_datetime", nullable = false,
            columnDefinition = "DATETIME DEFAULT NOW(6)")
    @Comment("취소시간")
    private LocalDateTime createdDateTime;

    @Comment("취소사유")
    @Column(name="cancel_reason", columnDefinition = "VARCHAR(255)")
    private String cancelReason;

    @Comment("취소금액")
    @Column(name="cancel_price", columnDefinition = "INT")
    private Integer cancelPrice;

    @OneToOne
    @JoinColumn
    private OrderItem orderItem;

}
