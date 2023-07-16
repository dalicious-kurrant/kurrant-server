package co.dalicious.domain.delivery.entity;

import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
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
@Table(name = "delivery__driver_schedule")
public class DriverSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment("배송 날짜")
    private LocalDate deliveryDate;

    @NotNull
    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "e_dining_type", nullable = false)
    @Comment("식사 타입")
    private DiningType diningType;

    @Comment("배송 시간")
    private LocalTime deliveryTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @OneToMany(mappedBy = "driverSchedule", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference(value = "driver_schedule_fk")
    private List<DriverRoute> driverRoutes;
}
