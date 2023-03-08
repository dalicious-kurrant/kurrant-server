package co.dalicious.domain.client.entity;

import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
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
    public ApartmentMealInfo(DiningType diningType, LocalTime deliveryTime, DayAndTime membershipBenefitTime, DayAndTime lastOrderTime, String serviceDays, Spot spot) {
        super(diningType, deliveryTime, membershipBenefitTime, lastOrderTime, serviceDays, spot);
    }

    public void updateApartmentMealInfo(GroupExcelRequestDto groupInfoList) {
        updateMealInfo(groupInfoList.getServiceDays());
    }
}
