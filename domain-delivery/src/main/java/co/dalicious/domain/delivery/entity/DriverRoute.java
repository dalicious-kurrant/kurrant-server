package co.dalicious.domain.delivery.entity;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.delivery.entity.converter.DeliveryStatusConverter;
import co.dalicious.domain.delivery.entity.enums.DeliveryStatus;
import co.dalicious.domain.food.entity.Makers;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery__driver_route", uniqueConstraints=@UniqueConstraint(columnNames={"group_id", "makers_id", "driver_schedule_id"}))
public class DriverRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Convert(converter = DeliveryStatusConverter.class)
    @Column(name = "e_delivery_status")
    private DeliveryStatus deliveryStatus;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    private Group group;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "makers_id")
    private Makers makers;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonManagedReference(value = "driver_schedule_fk")
    private DriverSchedule driverSchedule;

    public DriverRoute(DeliveryStatus deliveryStatus, Group group, Makers makers, DriverSchedule driverSchedule) {
        this.deliveryStatus = deliveryStatus;
        this.group = group;
        this.makers = makers;
        this.driverSchedule = driverSchedule;
    }
}
