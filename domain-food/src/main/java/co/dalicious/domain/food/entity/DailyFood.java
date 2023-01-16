package co.dalicious.domain.food.entity;

import co.dalicious.system.util.enums.DiningType;
import co.dalicious.system.util.enums.FoodStatus;
import co.dalicious.system.util.converter.DiningTypeConverter;
import co.dalicious.system.util.converter.FoodStatusConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;


@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "food__daily_food")
public class DailyFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "dining_type")
    private DiningType diningType;

    @CreationTimestamp
    @Column(name = "created")
    private LocalDate created;

    @UpdateTimestamp
    @Column(name = "updated")
    private LocalDate updated;

    @Convert(converter = FoodStatusConverter.class)
    @Column(name = "e_status")
    private FoodStatus status;

    @Column(name = "is_sold_out")
    private Boolean isSoldOut;

    @ManyToOne(optional = false)
    @JoinColumn(name = "food_id", columnDefinition = "BIGINT UNSIGNED")
    private Food food;

    @Column(name = "spot_id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger spotId;

    @Column(name = "service_date", columnDefinition = "DATE")
    private LocalDate serviceDate;

    @Builder
    DailyFood(BigInteger id, DiningType diningType, LocalDate created, LocalDate updated,
              FoodStatus status, Boolean isSoldOut, Food food, BigInteger spotId, LocalDate serviceDate){
        this.id = id;
        this.diningType = diningType;
        this.created = created;
        this.updated = updated;
        this.status = status;
        this.isSoldOut = isSoldOut;
        this.food = food;
        this.spotId = spotId;
        this.serviceDate = serviceDate;
    }

}
