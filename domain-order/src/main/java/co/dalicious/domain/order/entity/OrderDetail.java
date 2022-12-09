package co.dalicious.domain.order.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

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
    private Integer id;

    @CreationTimestamp
    @Column(name = "created", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Date created;

    @UpdateTimestamp
    @Column(name = "updated",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Date updated;

    @Column(name = "service_date", nullable = false,
            columnDefinition = "TIMESTAMP(6)")
    @Comment("서비스 날짜")
    private Date serviceDate;


    @Column(name = "is_check")
    @Comment("중복여부")
    private Boolean check;

    @Column(name = "price")
    @Comment("가격")
    private Integer price;

    @Column(name = "e_dining_type")
    @Comment("식사타입: 아침,점심,저녁")
    private String eDiningType;

    @Column(name = "count")
    @Comment("수량")
    private Integer count;

    @Column(name = "user__user_id")
    @Comment("사용자 ID")
    private Integer userId;

    @Column(name = "order__order_id")
    @Comment("주문 ID")
    private Integer orderId;

    @Column(name = "food__food_id")
    @Comment("식품 ID")
    private Integer foodId;
    @Builder
    public OrderDetail(Integer id, Date created, Date updated, Date serviceDate, Boolean check, Integer price, String eDiningType, Integer count, Integer userId, Integer orderId, Integer foodId) {
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
