package co.dalicious.domain.order.entity;

import co.dalicious.domain.order.converter.OrderStatusConverter;
import co.dalicious.system.util.DiningType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor
@Table(name = "order__cart_item")
public class OrderCartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("장바구니 상세 PK")
    private Integer id;

    @CreationTimestamp
    @Column(name = "created_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Date created;

    @UpdateTimestamp
    @Column(name = "updated_datetime",
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
    private DiningType diningType;

    @Column(name = "count")
    @Comment("수량")
    private Integer count;

    @Column(name = "order__cart_id")
    @Comment("주문 ID")
    private Integer cartId;

    @Column(name = "food__food_id")
    @Comment("식품 ID")
    private Integer foodId;

    @Builder
    public OrderCartItem(Integer id, Date created, Date updated, Date serviceDate,
                         Boolean check, Integer price, DiningType diningType, Integer count, Integer cartId, Integer foodId){
        this.id = id;
        this.created = created;
        this.updated = updated;
        this.serviceDate = serviceDate;
        this.check = check;
        this.price = price;
        this.diningType = diningType;
        this.count = count;
        this.cartId = cartId;
        this.foodId = foodId;
    }
}
