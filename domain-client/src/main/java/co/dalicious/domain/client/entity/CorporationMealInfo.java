package co.dalicious.domain.client.entity;

import co.dalicious.system.enums.DiningType;
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
}
