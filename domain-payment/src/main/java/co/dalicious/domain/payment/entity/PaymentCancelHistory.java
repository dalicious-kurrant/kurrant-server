package co.dalicious.domain.payment.entity;

import co.dalicious.domain.order.entity.OrderItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;

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
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_datetime",
            columnDefinition = "DATETIME")
    @Comment("취소시간")
    private LocalDateTime createdDateTime;

    @Comment("취소사유")
    @Column(name="cancel_reason", columnDefinition = "VARCHAR(255)")
    private String cancelReason;

    @Comment("취소금액")
    @Column(name="cancel_price", columnDefinition = "INT")
    private Integer cancelPrice;

    @Column(name = "refundable_price", columnDefinition = "")
    private Integer refundablePrice;

    @Comment("취소영수증 URL")
    @Column(name = "checkout_url", columnDefinition = "VARCHAR(255)")
    private String checkOutUrl;

    @Comment("토스 주문번호(OrderId)")
    @Column(name = "order_code", columnDefinition = "VARCHAR(255)")
    private String orderCode;

    @ManyToOne(optional = false)
    @JoinColumn
    private CreditCardInfo creditCardInfo;

    @OneToOne(optional = false)
    @JoinColumn
    private OrderItem orderItem;

    @Builder
    public PaymentCancelHistory(String cancelReason, Integer cancelPrice, Integer refundablePrice,
                                String checkOutUrl, String orderCode, CreditCardInfo creditCardInfo,
                                OrderItem orderItem){
        this.cancelReason = cancelReason;
        this.cancelPrice = cancelPrice;
        this.refundablePrice = refundablePrice;
        this.checkOutUrl = checkOutUrl;
        this.orderCode = orderCode;
        this.creditCardInfo = creditCardInfo;
        this.orderItem = orderItem;
    }

}
