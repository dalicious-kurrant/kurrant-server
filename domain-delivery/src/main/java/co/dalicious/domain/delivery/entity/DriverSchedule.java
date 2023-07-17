package co.dalicious.domain.delivery.entity;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery__driver_schedule", uniqueConstraints=@UniqueConstraint(columnNames={"delivery_date", "e_dining_type", "delivery_time", "driver_id"}))
public class DriverSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment("배송 날짜")
    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @NotNull
    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "e_dining_type", nullable = false)
    @Comment("식사 타입")
    private DiningType diningType;

    @Comment("배송 시간")
    @Column(name = "delivery_time", nullable = false)
    private LocalTime deliveryTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @OneToMany(mappedBy = "driverSchedule", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference(value = "driver_schedule_fk")
    private List<DriverRoute> driverRoutes;

    @Builder
    public DriverSchedule(LocalDate deliveryDate, DiningType diningType, LocalTime deliveryTime, Driver driver) {
        this.deliveryDate = deliveryDate;
        this.diningType = diningType;
        this.deliveryTime = deliveryTime;
        this.driver = driver;
    }

    public Group getGroup() {
        return this.driverRoutes.get(0).getGroup();
    }
}
