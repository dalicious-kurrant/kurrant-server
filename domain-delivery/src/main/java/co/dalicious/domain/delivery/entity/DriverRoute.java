package co.dalicious.domain.delivery.entity;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.delivery.entity.converter.DeliveryStatusConverter;
import co.dalicious.domain.delivery.entity.enums.DeliveryStatus;
import co.dalicious.domain.food.entity.Makers;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery__driver_route")
public class DriverRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Convert(converter = DeliveryStatusConverter.class)
    @Column(name = "e_delivery_status")
    private DeliveryStatus deliveryStatus;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id")
    private Spot spot;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "makers_id")
    private Makers makers;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonManagedReference(value = "driver_schedule_fk")
    private DriverSchedule driverSchedule;
}
