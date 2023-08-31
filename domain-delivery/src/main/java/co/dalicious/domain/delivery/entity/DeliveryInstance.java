package co.dalicious.domain.delivery.entity;

import co.dalicious.domain.client.entity.CorporationSpot;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.delivery.entity.converter.DeliveryStatusConverter;
import co.dalicious.domain.delivery.entity.enums.DeliveryStatus;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery__delivery_instance", uniqueConstraints=@UniqueConstraint(columnNames={"service_date", "e_dining_type", "delivery_time", "makers_id"}))
public class DeliveryInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Column(name = "service_date")
    private LocalDate serviceDate;
    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "e_dining_type")
    private DiningType diningType;

    @Convert(converter = DeliveryStatusConverter.class)
    @Column(name = "e_delivery_status", columnDefinition = "default 0")
    private DeliveryStatus deliveryStatus = DeliveryStatus.WAIT_DELIVERY;

    @Column(name = "delivery_time")
    private LocalTime deliveryTime;

    @Column(name = "order_number")
    private Integer orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "makers_id")
    private Makers makers;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id")
    private Spot spot;

    @OneToMany(mappedBy = "deliveryInstance", fetch = FetchType.LAZY)
    private List<DailyFoodDelivery> dailyFoodDeliveries;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Builder
    public DeliveryInstance(LocalDate serviceDate, DiningType diningType, LocalTime deliveryTime, Integer orderNumber, Makers makers, Spot spot) {
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.deliveryTime = deliveryTime;
        this.orderNumber = orderNumber;
        this.makers = makers;
        this.spot = spot;
    }

    public Integer getItemCount() {
        return dailyFoodDeliveries.stream()
                .filter(v -> OrderStatus.completePayment().contains(v.getOrderItemDailyFood().getOrderStatus()))
                .map(v -> v.getOrderItemDailyFood().getCount())
                .reduce(0, Integer::sum);
    }

    public List<OrderItemDailyFood> getOrderItemDailyFoods() {
        return dailyFoodDeliveries.stream()
                .map(DailyFoodDelivery::getOrderItemDailyFood)
                .toList();
    }

    public String getDeliveryCode() {
        return Hibernate.getClass(this.spot) == CorporationSpot.class
                ? spot.getId().toString()
                : DateUtils.formatWithoutSeparator(this.serviceDate) + this.makers.getId() + "-" + this.orderNumber;
    }

    public Integer getItemCount(DailyFood dailyFood) {
        return dailyFoodDeliveries.stream()
                .filter(v -> OrderStatus.completePayment().contains(v.getOrderItemDailyFood().getOrderStatus()) && v.getOrderItemDailyFood().getDailyFood().equals(dailyFood))
                .map(v -> v.getOrderItemDailyFood().getCount())
                .reduce(0, Integer::sum);
    }

    public LocalTime getPickupTime(LocalTime deliveryTime) {
        return this.getOrderItemDailyFoods().stream()
                .map(v -> v.getDailyFood().getDailyFoodGroup())
                .map(v -> v.getPickUpTime(deliveryTime) != null ? v.getPickUpTime(deliveryTime) : dailyFoodDeliveries.get(0).getOrderItemDailyFood().getDailyFood().getDailyFoodGroup().getExistPickUpTime())
                .findAny()
                .orElse(null);
    }

    public void updateDriver(Driver driver) {
        this.driver = driver;
    }

    public void updateDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
