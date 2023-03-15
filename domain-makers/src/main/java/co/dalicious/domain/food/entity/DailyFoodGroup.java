package co.dalicious.domain.food.entity;

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

    @Column(name = "pickup_time")
    @Comment("픽업시간")
    private LocalTime pickupTime;

    @OneToMany(mappedBy = "dailyFoodGroup")
    @JsonBackReference(value = "daily_food_group_fk")
    private List<DailyFood> dailyFoods;

    public DailyFoodGroup(LocalTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public void updatePickupTime(LocalTime pickupTime) {
        this.pickupTime = pickupTime;
    }
}
