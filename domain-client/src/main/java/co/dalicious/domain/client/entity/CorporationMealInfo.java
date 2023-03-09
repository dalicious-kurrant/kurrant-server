package co.dalicious.domain.client.entity;

import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CorporationMealInfo extends MealInfo{
    @NotNull
    @Column(name = "daily_support_price", nullable = false,columnDefinition = "DECIMAL(15, 2)")
    @Comment("식사 일정별(아침, 점심, 저녁) 식사 지원금")
    private BigDecimal supportPrice;

    @Builder
    public CorporationMealInfo(DiningType diningType, LocalTime deliveryTime, DayAndTime membershipBenefitTime, DayAndTime lastOrderTime, String serviceDays, Spot spot, BigDecimal supportPrice) {
        super(diningType, deliveryTime, membershipBenefitTime, lastOrderTime, serviceDays, spot);
        this.supportPrice = supportPrice;
    }

    public void updateCorporationMealInfo(CorporationMealInfo mealInfo) {
        super.updateMealInfo(mealInfo);
        this.supportPrice = mealInfo.getSupportPrice();
    }
    public void updateCorporationMealInfo(GroupExcelRequestDto groupInfoList) {
        updateMealInfo(groupInfoList.getServiceDays());

        BigDecimal supportPrice = BigDecimal.ZERO;
        if(groupInfoList.getMorningSupportPrice() != null && this.getDiningType().equals(DiningType.MORNING)) supportPrice = BigDecimal.valueOf(groupInfoList.getMorningSupportPrice());
        else if(groupInfoList.getLunchSupportPrice() != null && this.getDiningType().equals(DiningType.LUNCH)) supportPrice = BigDecimal.valueOf(groupInfoList.getLunchSupportPrice());
        else if(groupInfoList.getDinnerSupportPrice() != null && this.getDiningType().equals(DiningType.DINNER)) supportPrice = BigDecimal.valueOf(groupInfoList.getDinnerSupportPrice());

        this.supportPrice = supportPrice;
    }

    public void updateMealInfo(CorporationMealInfo mealInfo) {
        super.updateMealInfo(mealInfo);
        this.supportPrice = mealInfo.getSupportPrice();
    }
}
