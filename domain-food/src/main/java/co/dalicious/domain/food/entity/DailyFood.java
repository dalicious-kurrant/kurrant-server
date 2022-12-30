package co.dalicious.domain.food.entity;

import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.FoodStatus;
import co.dalicious.system.util.converter.DiningTypeConverter;
import jdk.jshell.Snippet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;


@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor
@Entity
@Table(name = "food__daily_food")
public class DailyFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "dining_type")
    private DiningType diningType;

    @CreationTimestamp
    @Column(name = "created")
    private LocalDate created;

    @UpdateTimestamp
    @Column(name = "updated")
    private LocalDate updated;

    @Column(name = "e_status")
    private String status;

    @Column(name = "is_sold_out")
    private Boolean isSoldOut;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "food__food_id")
    private Food food;

    @Column(name = "client__spot_id")
    private Integer spotId;

    @Column(name = "service_date", columnDefinition = "DATE")
    private LocalDate serviceDate;

    @Builder
    DailyFood(Integer id, DiningType diningType, LocalDate created, LocalDate updated,
              FoodStatus status, Boolean isSoldOut, Food food, Integer spotId, LocalDate serviceDate){
        this.id = id;
        this.diningType = diningType;
        this.created = created;
        this.updated = updated;
        this.status = status.getStatus();
        this.isSoldOut = isSoldOut;
        this.food = food;
        this.spotId = spotId;
        this.serviceDate = serviceDate;
    }

}
