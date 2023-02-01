package co.dalicious.domain.order.entity;

import co.dalicious.domain.payment.entity.CreditCardInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "payment__cancel_history")
public class PaymentCancelHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment("취소시간")
    private LocalDateTime cancelDateTime;

    @Comment("취소사유")
    @Column(name="cancel_reason", columnDefinition = "VARCHAR(255)")
    private String cancelReason;

    @Comment("취소금액")
    @Column(name="cancel_price", columnDefinition = "INT")
    private BigDecimal cancelPrice;

    @Column(name = "refundable_price", columnDefinition = "")
    private BigDecimal refundablePrice;

    @Comment("취소영수증 URL")
    @Column(name = "checkout_url", columnDefinition = "VARCHAR(255)")
    private String checkOutUrl;

    @Comment("토스 주문번호(OrderId)")
    @Column(name = "order_code", columnDefinition = "VARCHAR(255)")
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    private CreditCardInfo creditCardInfo;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    private OrderItem orderItem;

    @Builder
    public PaymentCancelHistory(String cancelReason, BigDecimal cancelPrice, BigDecimal refundablePrice,
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
