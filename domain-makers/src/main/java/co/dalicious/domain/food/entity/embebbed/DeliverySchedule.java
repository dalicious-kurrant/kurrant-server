package co.dalicious.domain.food.entity.embebbed;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class DeliverySchedule {
    @Column(name = "delivery_time")
    @Comment("배송 시간")
    private LocalTime deliveryTime;

    @Column(name = "pickup_time")
    @Comment("픽업 시간")
    private LocalTime pickupTime;

    @Builder
    public DeliverySchedule(LocalTime deliveryTime, LocalTime pickupTime) {
        this.deliveryTime = deliveryTime;
        this.pickupTime = pickupTime;
    }

    public void updateDeliveryTime(LocalTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void updatePickupTime(LocalTime pickupTime) {
        this.pickupTime = pickupTime;
    }
}
