package co.dalicious.domain.client.entity;

import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenGroupMealInfo extends MealInfo{
    @Builder
    public OpenGroupMealInfo(DiningType diningType, LocalTime deliveryTime, DayAndTime membershipBenefitTime, DayAndTime lastOrderTime, List<Days> serviceDays, Group group) {
        super(diningType, deliveryTime, membershipBenefitTime, lastOrderTime, serviceDays, group);
    }
}
