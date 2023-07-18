package co.dalicious.domain.order.dto;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.system.enums.DiningType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
public class SpotDeliveryTimeDto {

    private Spot spot;
    private LocalTime deliveryTime;

    public SpotDeliveryTimeDto(Spot spot, LocalTime deliveryTime) {
        this.spot = spot;
        this.deliveryTime = deliveryTime;
    }

    public boolean equals(Object obj) {
        if(obj instanceof SpotDeliveryTimeDto tmp) {
            return spot.equals(tmp.spot) && deliveryTime.equals(tmp.deliveryTime);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(spot, deliveryTime);
    }
}
