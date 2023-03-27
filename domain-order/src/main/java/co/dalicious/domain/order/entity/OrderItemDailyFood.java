package co.dalicious.domain.order.entity;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.util.PriceUtils;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "order__order_item_dailyfood")
public class OrderItemDailyFood extends OrderItem {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "daily_food_id")
    @Comment("식품 ID")
    private DailyFood dailyFood;

    @Comment("식품 이름")
    private String name;

    @Column(name = "price")
    @Comment("상품 가격")
    private BigDecimal price;

    @Column(name = "discounted_price")
    @Comment("할인된 가격")
    private BigDecimal discountedPrice;

    @Column(name = "count")
    @Comment("수량")
    private Integer count;

    @Column(name = "membership_discounted_rate")
    @Comment("멤버십 할인율")
    private Integer membershipDiscountRate;

    @Column(name = "makers_discounted_rate")
    @Comment("메이커스 할인율")
    private Integer makersDiscountRate;

    @Column(name = "period_discounted_rate")
    @Comment("기간 할인율")
    private Integer periodDiscountRate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "order_item_daily_food_group_fk")
    @Comment("식사 타입/배송날짜 기준 정기식사 주문 구분")
    private OrderItemDailyFoodGroup orderItemDailyFoodGroup;

    @Builder
    public OrderItemDailyFood(OrderStatus orderStatus, Order order, DailyFood dailyFood, String name, BigDecimal price, BigDecimal discountedPrice, Integer count, Integer makersDiscountRate, Integer membershipDiscountRate, Integer periodDiscountRate, OrderItemDailyFoodGroup orderItemDailyFoodGroup) {
        super(orderStatus, order);
        this.dailyFood = dailyFood;
        this.name = name;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.count = count;
        this.makersDiscountRate = makersDiscountRate;
        this.membershipDiscountRate = membershipDiscountRate;
        this.periodDiscountRate = periodDiscountRate;
        this.orderItemDailyFoodGroup = orderItemDailyFoodGroup;
    }

    public BigDecimal getMembershipDiscountPrice() {
        return this.price.multiply(BigDecimal.valueOf((this.membershipDiscountRate / 100.0))).multiply(BigDecimal.valueOf(this.count));
    }

    public BigDecimal getMakersDiscountPrice() {
        BigDecimal price = this.price;
        BigDecimal membershipDiscountedPrice = BigDecimal.ZERO;
        if(this.membershipDiscountRate != 0) {
            membershipDiscountedPrice = price.multiply(BigDecimal.valueOf((this.membershipDiscountRate / 100.0)));;
            price = price.subtract(membershipDiscountedPrice);
        }
        return price.multiply(BigDecimal.valueOf((this.makersDiscountRate / 100.0))).multiply(BigDecimal.valueOf(this.count));
    }

    public BigDecimal getPeriodDiscountPrice() {
        BigDecimal price = this.price;
        BigDecimal membershipDiscountedPrice = BigDecimal.ZERO;
        BigDecimal makersDiscountPrice = BigDecimal.ZERO;
        if(this.membershipDiscountRate != 0) {
            membershipDiscountedPrice = price.multiply(BigDecimal.valueOf((this.membershipDiscountRate / 100.0)));;
            price = price.subtract(membershipDiscountedPrice);
        }
        if(this.makersDiscountRate != 0) {
            makersDiscountPrice = price.multiply(BigDecimal.valueOf((this.makersDiscountRate / 100.0)));;
            price = price.subtract(makersDiscountPrice);
        }
        return price.multiply(BigDecimal.valueOf((this.periodDiscountRate / 100.0))).multiply(BigDecimal.valueOf(this.count));
    }

    public BigDecimal getOrderItemTotalPrice() {
        return this.discountedPrice.multiply(BigDecimal.valueOf(this.count));
    }
}
