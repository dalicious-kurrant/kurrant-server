package co.dalicious.domain.order.entity;

import co.dalicious.domain.order.converter.OrderStatusConverter;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
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
    private List<DailyFoodSupportPrice> userSupportPriceHistories;

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
        for (DailyFoodSupportPrice dailyFoodSupportPrice : this.userSupportPriceHistories) {
            if (dailyFoodSupportPrice.getMonetaryStatus().equals(MonetaryStatus.DEDUCTION)) {
                usingSupportPrice = usingSupportPrice.add(dailyFoodSupportPrice.getUsingSupportPrice());
            }
        }
        return usingSupportPrice;
    }

    public BigDecimal getTotalPriceByGroup() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        if (this.orderStatus.equals(OrderStatus.CANCELED)) return totalPrice;

        for (OrderItemDailyFood orderDailyFood : this.orderDailyFoods) {
            totalPrice = totalPrice.add((OrderStatus.completePayment().contains(orderDailyFood.getOrderStatus())) ? orderDailyFood.getOrderItemTotalPrice() : BigDecimal.ZERO);
        }
        totalPrice = totalPrice.add(this.deliveryFee);
        return totalPrice;
    }

    public BigDecimal getPayPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        // 1. 배송비 추가
        totalPrice = totalPrice.add(this.getDeliveryFee());

        // 2. 할인된 상품 가격 추가
        for (OrderItemDailyFood orderItemDailyFood : this.getOrderDailyFoods()) {
            if (OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus())) {
                totalPrice = totalPrice.add(orderItemDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(orderItemDailyFood.getCount())));
            }
        }
        // 3. 지원금 사용 가격 제외
        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(this.getUserSupportPriceHistories());
        totalPrice = totalPrice.subtract(usedSupportPrice);

        // 예외. 포인트 사용으로 인해 식사 일정별 환불 가능 금액이 주문 전체 금액이 더 작을 경우
        Order order = this.getOrderDailyFoods().get(0).getOrder();
        if (order.getTotalPrice().compareTo(totalPrice) < 0) {
            return order.getTotalPrice();
        }

        return totalPrice;
    }

    public Boolean isMembershipApplied() {
        Optional<OrderItemDailyFood> orderItemDailyFood = this.orderDailyFoods.stream()
                .filter(v -> v.getMembershipDiscountRate() != null && v.getMembershipDiscountRate() > 0)
                .findFirst();
        return orderItemDailyFood.isPresent();
    }

    public List<OrderItemDailyFood> getOrderItemDailyFoodInOrderComplete() {
        return this.orderDailyFoods.stream()
                .filter(v -> OrderStatus.completePayment().contains(v.getOrderStatus()))
                .toList();
    }
}
