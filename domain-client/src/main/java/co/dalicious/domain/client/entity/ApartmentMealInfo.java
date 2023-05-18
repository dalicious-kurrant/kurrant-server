package co.dalicious.domain.client.entity;

import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.entity.embeddable.DeliverySchedule;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApartmentMealInfo extends MealInfo {
    @Builder
    public ApartmentMealInfo(DiningType diningType, List<DeliverySchedule> deliveryScheduleList, DayAndTime membershipBenefitTime, DayAndTime lastOrderTime, List<Days> serviceDays, Group group) {
        super(diningType, deliveryScheduleList, membershipBenefitTime, lastOrderTime, serviceDays, group);
    }
}
