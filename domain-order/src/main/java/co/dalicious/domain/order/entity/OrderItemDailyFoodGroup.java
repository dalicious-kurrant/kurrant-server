package co.dalicious.domain.order.entity;

import co.dalicious.domain.order.converter.OrderStatusConverter;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "order__order_item_dailyfood_group")
public class OrderItemDailyFoodGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("주문상세 PK")
    @Column(name="id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Convert(converter = OrderStatusConverter.class)
    @Column(name = "e_order_status")
    @Comment("결제 진행 상태")
    private OrderStatus orderStatus;

    @Comment("배송 날짜")
    private LocalDate serviceDate;

    @Column(name = "e_dining_type")
    @Convert(converter = DiningTypeConverter.class)
    @Comment("식사 타입")
    private DiningType diningType;

    @Column(columnDefinition = "DECIMAL(15, 2)")
    @Comment("배송비")
    private BigDecimal deliveryFee;

    @OneToMany(mappedBy = "orderItemDailyFoodGroup", orphanRemoval = true)
    @JsonBackReference(value = "order_item_daily_food_group_fk")
    @Comment("정기식사에 사용된 지원금")
    private List<UserSupportPriceHistory> userSupportPriceHistories;

    @OneToMany(mappedBy = "orderItemDailyFoodGroup", cascade = CascadeType.ALL)
    @JsonBackReference(value = "order_item_daily_food_group_fk")
    private List<OrderItemDailyFood> orderDailyFoods;

    @Builder
    public OrderItemDailyFoodGroup(OrderStatus orderStatus, LocalDate serviceDate, DiningType diningType, BigDecimal deliveryFee) {
        this.orderStatus = orderStatus;
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.deliveryFee = deliveryFee;
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getUsingSupportPrice() {
        BigDecimal usingSupportPrice = BigDecimal.ZERO;
        for (UserSupportPriceHistory userSupportPriceHistory : this.userSupportPriceHistories) {
            if (userSupportPriceHistory.getMonetaryStatus().equals(MonetaryStatus.DEDUCTION)) {
                usingSupportPrice = usingSupportPrice.add(userSupportPriceHistory.getUsingSupportPrice());
            }
        }
        return usingSupportPrice;
    }

    public BigDecimal getTotalPriceByGroup() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        if(this.orderStatus.equals(OrderStatus.CANCELED)) return totalPrice;

        for (OrderItemDailyFood orderDailyFood : this.orderDailyFoods) {
            totalPrice = totalPrice.add((OrderStatus.completePayment().contains(orderDailyFood.getOrderStatus())) ? orderDailyFood.getDiscountedPrice() : BigDecimal.ZERO);
        }
        totalPrice = totalPrice.add(this.deliveryFee);
        totalPrice = totalPrice.subtract(getUsingSupportPrice());
        return totalPrice;
    }
}
