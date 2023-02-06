package co.dalicious.domain.order.entity;

import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.converter.RefundPriceDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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
    @Column(columnDefinition = "VARCHAR(255)")
    private String cancelReason;

    @Comment("포인트 환불 금액")
    @Column(columnDefinition = "DECIMAL(15, 2)")
    private BigDecimal refundPointPrice;

    @Comment("배송비 환불 금액")
    @Column(columnDefinition = "DECIMAL(15, 2)")
    private BigDecimal refundDeliveryFee;

    @Comment("카드 환불 금액")
    @Column(columnDefinition = "DECIMAL(15, 2)")
    private BigDecimal cancelPrice;

    @Comment("환불 가능한 금액")
    @Column(columnDefinition = "DECIMAL(15, 2)")
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    private Order order;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    private OrderItem orderItem;


    public PaymentCancelHistory(String cancelReason, RefundPriceDto refundPriceDto, OrderItemDailyFood orderDailyItemFood, String checkOutUrl, String orderCode, BigDecimal refundablePrice, CreditCardInfo creditCardInfo) {
        this.cancelDateTime = LocalDateTime.now();
        this.cancelReason = cancelReason;
        this.refundPointPrice = refundPriceDto.getPoint();
        this.cancelPrice = refundPriceDto.getPrice();
        this.refundDeliveryFee = refundPriceDto.getDeliveryFee();
        this.refundablePrice = refundablePrice;
        this.checkOutUrl = checkOutUrl;
        this.orderCode = orderCode;
        this.creditCardInfo = creditCardInfo;
        this.order = orderDailyItemFood.getOrder();
        this.orderItem = orderDailyItemFood;
    }
}
