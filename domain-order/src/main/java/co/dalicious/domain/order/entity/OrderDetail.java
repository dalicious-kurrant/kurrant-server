package co.dalicious.domain.order.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor
@Table(name = "order__order_detail")
public class OrderDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
  @Comment("주문상세 PK")
  private BigInteger id;

  @CreationTimestamp
  @Column(name = "created", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
  @Comment("생성일")
  private Date created;

  @UpdateTimestamp
  @Column(name = "updated", columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
  @Comment("수정일")
  private Date updated;

  @Column(name = "service_date", nullable = false, columnDefinition = "TIMESTAMP(6)")
  @Comment("서비스 날짜")
  private Date serviceDate;

  @Column(name = "is_check")
  @Comment("중복여부")
  private Boolean check;

  @Column(name = "price", precision = 15)
  @Comment("가격")
  private BigDecimal price;

  @Column(name = "e_dining_type")
  @Comment("식사타입: 아침,점심,저녁")
  private String eDiningType;

  @Column(name = "count")
  @Comment("수량")
  private Integer count;

  @Column(name = "user__user_id")
  @Comment("사용자 ID")
  private Integer userId;

  @Comment("주문 FK")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order__order_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
  private Order order;
  @Column(name = "order__order_id", nullable = false, insertable = false, updatable = false,
      columnDefinition = "BIGINT UNSIGNED")
  private BigInteger orderId;

  @Column(name = "food__food_id")
  @Comment("식품 ID")
  private Integer foodId;

  @Comment("식품 명")
  @Column(name = "food_name", nullable = false, columnDefinition = "VARCHAR(64)")
  private String foodName;

  @Comment("메이커스 명")
  @Column(name = "makers_name", nullable = false, columnDefinition = "VARCHAR(64)")
  private String makersName;

  @Builder
  public OrderDetail(BigInteger id, Date created, Date updated, Date serviceDate, Boolean check,
      BigDecimal price, String eDiningType, Integer count, Integer userId, BigInteger orderId,
      Integer foodId) {
    this.id = id;
    this.created = created;
    this.updated = updated;
    this.serviceDate = serviceDate;
    this.check = check;
    this.price = price;
    this.eDiningType = eDiningType;
    this.count = count;
    this.userId = userId;
    this.orderId = orderId;
    this.foodId = foodId;
  }
}
