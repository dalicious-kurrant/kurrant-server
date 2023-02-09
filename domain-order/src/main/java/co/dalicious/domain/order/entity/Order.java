package co.dalicious.domain.order.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import co.dalicious.domain.order.converter.OrderStatusConverter;
import co.dalicious.domain.order.converter.OrderTypeConverter;
import co.dalicious.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order__order")
public class Order {
  @Id
  @Column(name = "id", nullable = false)
  private BigInteger id;

  @NotNull
  @Column(name = "code")
  private String code;

  @Convert(converter = OrderTypeConverter.class)
  @Column(name = "e_order_type")
  @Comment("주문 타입(정기식사/멤버십/상품)")
  private OrderType orderType;

  @Convert(converter = OrderStatusConverter.class)
  @Column(name = "e_order_status")
  @Comment("결제 진행 상태")
  private OrderStatus orderStatus;

  @NotNull
  @Column(name = "total_price", nullable = false, precision = 15)
  private BigDecimal totalPrice;

  @Size(max = 8)
  @NotNull
  @Column(name = "e_payment_type", nullable = false, length = 8)
  private String ePaymentType;

  @CreationTimestamp
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss",
      timezone = "Asia/Seoul")
  @Column(name = "created_datetime", nullable = false,
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
  @Comment("생성일")
  private Timestamp createdDateTime;

  @UpdateTimestamp
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss",
      timezone = "Asia/Seoul")
  @Column(name = "updated_datetime", columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
  @Comment("수정일")
  private Timestamp updatedDateTime;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Comment("주문자명")
  @Column(name = "orderer_name", nullable = false, columnDefinition = "VARCHAR(64)")
  private String ordererName;

  public void updateStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  @Builder
  public Order(String code, OrderType orderType, OrderStatus orderStatus, BigDecimal totalPrice,
      String ePaymentType, User user) {
    this.code = code;
    this.orderType = orderType;
    this.orderStatus = orderStatus;
    this.totalPrice = totalPrice;
    this.ePaymentType = ePaymentType;
    this.user = user;
  }
}
