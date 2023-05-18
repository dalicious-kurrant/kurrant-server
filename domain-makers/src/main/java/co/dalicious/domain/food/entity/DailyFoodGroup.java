package co.dalicious.domain.food.entity;

import co.dalicious.domain.client.entity.embeddable.DeliverySchedule;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "food__daily_food_group")
public class DailyFoodGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Embedded
    @Comment("배송 시간")
    private DeliverySchedule deliverySchedule;

    @OneToMany(mappedBy = "dailyFoodGroup")
    @JsonBackReference(value = "daily_food_group_fk")
    private List<DailyFood> dailyFoods;

    public DailyFoodGroup(DeliverySchedule deliverySchedule) {
        this.deliverySchedule = deliverySchedule;
    }

    public void updateDeliverySchedule(DeliverySchedule deliverySchedule) {
        this.deliverySchedule = deliverySchedule;
    }

    public void updatePickupTime(LocalTime pickupTime) {
        this.deliverySchedule.updatePickupTime(pickupTime);
    }
}
