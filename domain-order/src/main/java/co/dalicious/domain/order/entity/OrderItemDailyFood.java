package co.dalicious.domain.order.entity;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.util.NumberUtils;
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
import java.time.LocalTime;

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

    @Comment("배송 시간")
    private LocalTime deliveryTime;

    @Comment("식품 이름")
    private String name;

    @Column(name = "price", columnDefinition="Decimal(15,2) default '0.00'")
    @Comment("상품 가격")
    private BigDecimal price;

    @Column(name = "discounted_price", columnDefinition = "DECIMAL(15, 2)")
    @Comment("할인된 가격")
    private BigDecimal discountedPrice;

    @Column(name = "count")
    @Comment("수량")
    private Integer count;

    @Comment("추가 주문 사용 목적")
    @Column(name = "usage", columnDefinition = "VARCHAR(40)")
    private String usage;

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
    public OrderItemDailyFood(OrderStatus orderStatus, Order order, DailyFood dailyFood, LocalTime deliveryTime, String name, BigDecimal price, BigDecimal discountedPrice, Integer count, String usage, Integer membershipDiscountRate, Integer makersDiscountRate, Integer periodDiscountRate, OrderItemDailyFoodGroup orderItemDailyFoodGroup) {
        super(orderStatus, order);
        this.dailyFood = dailyFood;
        this.deliveryTime = deliveryTime;
        this.name = name;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.count = count;
        this.usage = usage;
        this.membershipDiscountRate = membershipDiscountRate;
        this.makersDiscountRate = makersDiscountRate;
        this.periodDiscountRate = periodDiscountRate;
        this.orderItemDailyFoodGroup = orderItemDailyFoodGroup;
    }

    public BigDecimal getMembershipDiscountPrice() {
        return NumberUtils.roundToTenDigit(this.price.multiply(BigDecimal.valueOf((this.membershipDiscountRate / 100.0))).multiply(BigDecimal.valueOf(this.count)));
    }

    public BigDecimal getMakersDiscountPrice() {
        BigDecimal price = this.price;
        BigDecimal membershipDiscountedPrice = BigDecimal.ZERO;
        if (this.membershipDiscountRate != 0) {
            membershipDiscountedPrice = price.multiply(BigDecimal.valueOf((this.membershipDiscountRate / 100.0)));
            ;
            price = price.subtract(membershipDiscountedPrice);
        }
        return NumberUtils.roundToTenDigit(price.multiply(BigDecimal.valueOf((this.makersDiscountRate / 100.0))).multiply(BigDecimal.valueOf(this.count)));
    }

    public BigDecimal getPeriodDiscountPrice() {
        BigDecimal price = this.price;
        BigDecimal membershipDiscountedPrice = BigDecimal.ZERO;
        BigDecimal makersDiscountPrice = BigDecimal.ZERO;
        if (this.membershipDiscountRate != 0) {
            membershipDiscountedPrice = price.multiply(BigDecimal.valueOf((this.membershipDiscountRate / 100.0)));
            ;
            price = price.subtract(membershipDiscountedPrice);
        }
        if (this.makersDiscountRate != 0) {
            makersDiscountPrice = price.multiply(BigDecimal.valueOf((this.makersDiscountRate / 100.0)));
            ;
            price = price.subtract(makersDiscountPrice);
        }
        return NumberUtils.roundToTenDigit(price.multiply(BigDecimal.valueOf((this.periodDiscountRate / 100.0))).multiply(BigDecimal.valueOf(this.count)));
    }

    public BigDecimal getOrderItemTotalPrice() {
        return this.discountedPrice.multiply(BigDecimal.valueOf(this.count));
    }

    public BigDecimal getOrderItemSupplyPrice() {
        if(this.getDailyFood().getSupplyPrice() != null) {
            return this.getDailyFood().getSupplyPrice().multiply(BigDecimal.valueOf(this.count));
        }
        if(this.getDailyFood().getFood().getSupplyPrice() != null) {
            return this.getDailyFood().getFood().getSupplyPrice().multiply(BigDecimal.valueOf(this.count));
        }
        return null;
    }

    public String getLastOrderTime(){

        DayAndTime makersLastOrderTime = this.dailyFood.getFood().getMakers().getMakersCapacity(this.dailyFood.getDiningType()).getLastOrderTime();
        DayAndTime mealInfoLastOrderTIme = this.dailyFood.getGroup().getMealInfo(this.dailyFood.getDiningType()).getLastOrderTime();

        //메이커스의 주문 마감시간이 null이 아니고, 밀인포 마감시간 보다 빠를때는 메이커스 마감시간을 리턴한다.
        if (makersLastOrderTime != null && DayAndTime.toLocalDate(makersLastOrderTime).isBefore(DayAndTime.toLocalDate(mealInfoLastOrderTIme))){
            return makersLastOrderTime.dayAndTimeToStringByDate(this.dailyFood.getServiceDate());
        }
        return mealInfoLastOrderTIme.dayAndTimeToStringByDate(this.dailyFood.getServiceDate());

    }

}
