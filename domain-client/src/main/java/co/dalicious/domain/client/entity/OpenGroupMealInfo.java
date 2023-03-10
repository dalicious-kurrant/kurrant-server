package co.dalicious.domain.client.entity;

import co.dalicious.domain.client.dto.GroupExcelRequestDto;
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
    public OpenGroupMealInfo(DiningType diningType, LocalTime deliveryTime, DayAndTime membershipBenefitTime, DayAndTime lastOrderTime, String serviceDays, Group group) {
        super(diningType, deliveryTime, membershipBenefitTime, lastOrderTime, serviceDays, group);
    }

    public void updateOpenGroupMealInfo(GroupExcelRequestDto groupInfoList) {
        updateMealInfo(groupInfoList.getServiceDays());
    }
}
