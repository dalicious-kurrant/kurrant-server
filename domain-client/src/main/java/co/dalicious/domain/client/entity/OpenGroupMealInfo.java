package co.dalicious.domain.client.entity;

import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenGroupMealInfo extends MealInfo{
    @Builder
    public OpenGroupMealInfo(DiningType diningType, LocalTime deliveryTime, LocalTime membershipBenefitTime, LocalTime lastOrderTime, String serviceDays, Spot spot) {
        super(diningType, deliveryTime, membershipBenefitTime, lastOrderTime, serviceDays, spot);
    }
}