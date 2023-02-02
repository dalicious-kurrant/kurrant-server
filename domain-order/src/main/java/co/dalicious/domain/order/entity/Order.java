package co.dalicious.domain.order.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.order.converter.OrderTypeConverter;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.converter.PaymentTypeConverter;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Table(name = "order__order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Convert(converter = OrderTypeConverter.class)
    @Column(name = "e_order_type")
    @Comment("주문 타입: 정기식사 결제(1), 마켓 결제(2), 멤버십 결제(3)")
    private OrderType orderType;

    @NotNull
    @Column(name = "code")
    private String code;

    @Embedded
    @Comment("배송지")
    private Address address;

    @Column(name = "default_price", precision = 15)
    @Comment("상품 총액(할인되지 않은 가격)")
    private BigDecimal defaultPrice;

    @Column(name = "point", precision = 15)
    @Comment("포인트 사용 금액")
    private BigDecimal point;

    @Column(name = "total_price", precision = 15)
    @Comment("결제 총액(모든 할인이 들어간 가격)")
    private BigDecimal totalPrice;

    @Convert(converter = PaymentTypeConverter.class)
    @Column(name = "e_payment_type")
    @Comment("결제 타입")
    private PaymentType paymentType;


    @Column(name = "payment_key", columnDefinition = "VARCHAR(255)")
    @Comment("토스 조회용 페이먼트키")
    private String paymentKey;

    @Column(name = "receipt_url", columnDefinition = "VARCHAR(255)")
    @Comment("영수증 URL")
    private String receiptUrl;

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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order")
    @JsonBackReference(value = "order_fk")
    List<OrderItem> orderItems;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn
    private CreditCardInfo creditCardInfo;

    public Order(String code, PaymentType paymentType, OrderType orderType, CreditCardInfo creditCardInfo) {
        this.orderType = orderType;
        this.code = code;
        this.paymentType = paymentType;
        this.creditCardInfo = creditCardInfo;
    }

    public Order(String code, OrderType orderType, Address address, PaymentType paymentType, User user) {
        this.code = code;
        this.orderType = orderType;
        this.address = address;
        this.paymentType = paymentType;
        this.user = user;
    }

    public void updateOrderUserInfo(OrderUserInfoDto orderUserInfoDto) {
        this.address = orderUserInfoDto.getAddress();
        this.user = orderUserInfoDto.getUser();
    }

    public void updateDefaultPrice(BigDecimal defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public void updatePoint(BigDecimal point) {
        this.point = point;
    }

    public void updatePaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public void updateTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}