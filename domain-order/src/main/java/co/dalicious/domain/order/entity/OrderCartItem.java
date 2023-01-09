package co.dalicious.domain.order.entity;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.system.util.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order__cart_item")
public class OrderCartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("장바구니 상세 PK")
    @NotNull
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @CreationTimestamp
    @Column(name = "created_datetime",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private LocalDate created;

    @UpdateTimestamp
    @Column(name = "updated_datetime",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private LocalDate updated;

    @Column(name = "service_date", nullable = false,
            columnDefinition = "TIMESTAMP(6)")
    @Comment("서비스 날짜")
    private LocalDate serviceDate;

    @Column(name = "price")
    @Comment("가격")
    private Integer price;

    @Column(name = "e_dining_type")
    @Comment("식사타입: 아침,점심,저녁")
    private DiningType diningType;

    @Column(name = "count")
    @Comment("수량")
    private Integer count;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "cart_id")
    @Comment("장바구니 ID")
    private OrderCart orderCart;

    @ManyToOne
    @JoinColumn(name="dailyFood_id")
    @Comment("장바구니에 담긴 음식 ID")
    private DailyFood dailyFood;

    @Builder
    public OrderCartItem( LocalDate serviceDate, Integer price, DiningType diningType,
                          Integer count, OrderCart orderCart, DailyFood dailyFood){
        this.serviceDate = serviceDate;
        this.price = price;
        this.diningType = diningType;
        this.count = count;
        this.orderCart = orderCart;
        this.dailyFood = dailyFood;
    }
}
