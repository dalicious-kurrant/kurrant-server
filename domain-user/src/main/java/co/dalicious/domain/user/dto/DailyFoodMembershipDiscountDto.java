package co.dalicious.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DailyFoodMembershipDiscountDto {
    private BigDecimal totalMembershipDiscountPrice;
    private BigDecimal totalMembershipDiscountDeliveryFee;

    public DailyFoodMembershipDiscountDto(BigDecimal totalMembershipDiscountPrice, BigDecimal totalMembershipDiscountDeliveryFee) {
        this.totalMembershipDiscountPrice = totalMembershipDiscountPrice;
        this.totalMembershipDiscountDeliveryFee = totalMembershipDiscountDeliveryFee;
    }
}
