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
    @Column(name = "daily_support_price", columnDefinition = "DECIMAL(15, 2)")
    @Comment("식사 일정별(아침, 점심, 저녁) 회사 지원금")
    private BigDecimal supportPrice;

    @Builder
    public CorporationMealInfo(DiningType diningType, LocalTime deliveryTime, LocalTime membershipBenefitTime, LocalTime lastOrderTime, String serviceDays, Spot spot, BigDecimal supportPrice) {
        super(diningType, deliveryTime, membershipBenefitTime, lastOrderTime, serviceDays, spot);
        this.supportPrice = supportPrice;
    }

    public void updateCorporationMealInfo(GroupExcelRequestDto groupInfoList) {
        updateMealInfo(DateUtils.stringToLocalTime(groupInfoList.getMembershipBenefitTime()), groupInfoList.getServiceDays());

        BigDecimal supportPrice = BigDecimal.ZERO;
        if(groupInfoList.getMorningSupportPrice() != null && !groupInfoList.getMorningSupportPrice().isEmpty() &&
                !groupInfoList.getMorningSupportPrice().isBlank() && !groupInfoList.getMorningSupportPrice().equals("null") &&
                this.getDiningType().equals(DiningType.MORNING)) supportPrice = BigDecimal.valueOf(Double.parseDouble(groupInfoList.getMorningSupportPrice()));

        else if(groupInfoList.getLunchSupportPrice() != null && !groupInfoList.getLunchSupportPrice().isEmpty() &&
                !groupInfoList.getLunchSupportPrice().isBlank() && !groupInfoList.getLunchSupportPrice().equals("null") &&
                this.getDiningType().equals(DiningType.LUNCH)) supportPrice = BigDecimal.valueOf(Double.parseDouble(groupInfoList.getLunchSupportPrice()));

        else if(groupInfoList.getDinnerSupportPrice() != null && !groupInfoList.getDinnerSupportPrice().isEmpty() &&
                !groupInfoList.getDinnerSupportPrice().isBlank() && !groupInfoList.getDinnerSupportPrice().equals("null") &&
                this.getDiningType().equals(DiningType.DINNER)) supportPrice = BigDecimal.valueOf(Double.parseDouble(groupInfoList.getDinnerSupportPrice()));

        this.supportPrice = supportPrice;
    }
}
