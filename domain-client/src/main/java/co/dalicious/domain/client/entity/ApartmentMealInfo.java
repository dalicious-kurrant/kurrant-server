package co.dalicious.domain.client.entity;

import co.dalicious.system.util.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApartmentMealInfo extends MealInfo {
    @Builder
    public ApartmentMealInfo(DiningType diningType, LocalTime deliveryTime, LocalTime lastOrderTime, String serviceDays, Spot spot) {
        super(diningType, deliveryTime, lastOrderTime, serviceDays, spot);
    }
}
