package co.dalicious.domain.food.entity;

import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
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

    @ElementCollection
    @Comment("배송 시간")
    @CollectionTable(name = "food__delivery_schedule")
    private List<DeliverySchedule> deliverySchedules;

    @OneToMany(mappedBy = "dailyFoodGroup")
    @JsonBackReference(value = "daily_food_group_fk")
    private List<DailyFood> dailyFoods;

    public DailyFoodGroup(List<DeliverySchedule> deliverySchedules) {
        this.deliverySchedules = deliverySchedules;
    }

    public void updateDeliverySchedules(List<DeliverySchedule> deliverySchedules) {
        this.deliverySchedules = deliverySchedules;
    }

    public void updatePickupTime(LocalTime pickupTime, LocalTime deliveryTime) {
        this.deliverySchedules.forEach(deliverySchedule -> {
            if(deliverySchedule.getDeliveryTime().equals(deliveryTime)) {
                deliverySchedule.updatePickupTime(pickupTime);
            }
        });

    }
}
