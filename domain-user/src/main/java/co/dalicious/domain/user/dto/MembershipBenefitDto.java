package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MembershipBenefitDto {
    @Schema(description = "다을 결제 예정일")
    private String nextPayDate;
    private String membershipSubscriptionType;
    private BigDecimal deliveryFee;
    private BigDecimal dailyFoodDiscountPrice;
    private BigDecimal productDiscountPrice;
    private BigDecimal totalDiscountBenefitPrice;
    private BigDecimal dailyFoodReviewPoint;
    private BigDecimal productReviewPoint;
    private BigDecimal productBuyPoint;
    private BigDecimal totalPointBenefitPrice;
    private BigDecimal membershipRefundablePrice;
}
