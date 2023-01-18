package co.dalicious.domain.order.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.order.converter.OrderTypeConverter;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.user.converter.PaymentTypeConverter;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "order__order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @NotNull
    @Column(name = "code")
    private String code;

    @Convert(converter = OrderTypeConverter.class)
    @Column(name = "e_order_type")
    @Comment("주문 타입(정기식사/멤버십/상품)")
    private OrderType orderType;

    @Comment("그룹명")
    private String groupName;

    @Comment("스팟명")
    private String spotName;

    @Comment("상세주소")
    private String ho;

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

    @Builder
    public Order(String code, OrderType orderType, PaymentType paymentType) {
        this.code = code;
        this.orderType = orderType;
        this.paymentType = paymentType;
    }

    public void updateOrderUserInfo(OrderUserInfoDto orderUserInfoDto) {
        this.groupName = orderUserInfoDto.getGroupName();
        this.spotName = orderUserInfoDto.getSpotName();
        this.ho = orderUserInfoDto.getSpotName();
        this.address = orderUserInfoDto.getAddress();
        this.user = orderUserInfoDto.getUser();
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public void setDefaultPrice(BigDecimal defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public void setPoint(BigDecimal point) {
        this.point = point;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}