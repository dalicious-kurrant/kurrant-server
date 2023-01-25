package co.dalicious.domain.order.entity;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.util.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "order__order_item_dailyfood")
public class OrderItemDailyFood extends OrderItem{
    @Column(name = "service_date", nullable = false,
            columnDefinition = "TIMESTAMP(6)")
    @Comment("서비스 날짜")
    private LocalDate serviceDate;

    @Column(name = "e_dining_type")
    @Comment("식사타입: 아침,점심,저녁")
    private DiningType diningType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id", nullable = false)
    @Comment("식품 ID")
    private Food food;

    @Comment("식품 이름")
    private String name;

    @Column(name = "price")
    @Comment("상품 가격")
    private BigDecimal price;

    @Column(name = "discounted_price")
    @Comment("상품 총 할인 가격")
    private BigDecimal discountedPrice;

    @Column(name = "count")
    @Comment("수량")
    private Integer count;

    @Column(name = "makers_discounted_rate")
    @Comment("메이커스 할인율")
    private Integer makersDiscountedRate;

    @Column(name = "membership_discounted_rate")
    @Comment("멤버십 할인율")
    private Integer membershipDiscountedRate;

    @Column(name = "period_discounted_rate")
    @Comment("기간 할인율")
    private Integer periodDiscountedRate;

    @Builder
    public OrderItemDailyFood(OrderStatus orderStatus, Order order, LocalDate serviceDate, DiningType diningType, Food food, String name, BigDecimal price, BigDecimal discountedPrice, Integer count, Integer makersDiscountedRate, Integer membershipDiscountedRate, Integer periodDiscountedRate) {
        super(orderStatus, order);
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.food = food;
        this.name = name;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.count = count;
        this.makersDiscountedRate = makersDiscountedRate;
        this.membershipDiscountedRate = membershipDiscountedRate;
        this.periodDiscountedRate = periodDiscountedRate;
    }
}
