package co.dalicious.domain.food.entity;

import co.dalicious.system.util.FoodStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;


@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor
@Entity
@Table(name = "food__daily_food")
public class DailyFood {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "dining_type")
    private String diningType;

    @CreationTimestamp
    @Column(name = "created")
    private LocalDate created;

    @UpdateTimestamp
    @Column(name = "updated")
    private LocalDate updated;

    @Column(name = "e_status")
    private FoodStatus status;

    @Column(name = "isSoldOut")
    private Integer isSoldOut;

    @Column(name = "food__food_id")
    private Integer foodId;

    @Column(name = "client__spot_id")
    private Integer spotId;

}
