package co.dalicious.domain.delivery.entity;

import co.dalicious.domain.order.entity.OrderItemDailyFood;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "delivery__daily_food_delivery")
public class DailyFoodDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private DeliveryInstance deliveryInstance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private OrderItemDailyFood orderItemDailyFood;

    public DailyFoodDelivery(DeliveryInstance deliveryInstance, OrderItemDailyFood orderItemDailyFood) {
        this.deliveryInstance = deliveryInstance;
        this.orderItemDailyFood = orderItemDailyFood;
    }
}
