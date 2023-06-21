package co.dalicious.domain.delivery.entity;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery__delivery_instance")
public class DeliveryInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    private LocalDate serviceDate;
    @Convert(converter = DiningTypeConverter.class)
    private DiningType diningType;
    private LocalTime deliveryTime;
    private LocalTime pickUpTime;
    private Integer orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Makers makers;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Spot spot;

    @OneToMany(mappedBy = "deliveryInstance", fetch = FetchType.LAZY)
    private List<DailyFoodDelivery> dailyFoodDeliveries;

    @Builder
    public DeliveryInstance(LocalDate serviceDate, DiningType diningType, LocalTime deliveryTime, LocalTime pickUpTime, Integer orderNumber, Makers makers, Spot spot) {
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.deliveryTime = deliveryTime;
        this.pickUpTime = pickUpTime;
        this.orderNumber = orderNumber;
        this.makers = makers;
        this.spot = spot;
    }

    public Integer getItemCount() {
        return dailyFoodDeliveries.stream()
                .map(v -> v.getOrderItemDailyFood().getCount())
                .reduce(0, Integer::sum);
    }

    public List<OrderItemDailyFood> getOrderItemDailyFoods() {
        return dailyFoodDeliveries.stream()
                .map(DailyFoodDelivery::getOrderItemDailyFood)
                .toList();
    }
}
