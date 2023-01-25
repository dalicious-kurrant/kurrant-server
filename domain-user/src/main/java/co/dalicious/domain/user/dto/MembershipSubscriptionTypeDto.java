package co.dalicious.domain.user.dto;

import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Schema(description = "멤버십 구독 정보 응답 DTO")
@Getter
@Setter
public class MembershipSubscriptionTypeDto {
    private String membershipSubscriptionType;
    private BigDecimal price;
    private int discountRate;
    private BigDecimal discountedPrice;

    @Builder
    public MembershipSubscriptionTypeDto(MembershipSubscriptionType membershipSubscriptionType) {
        this.membershipSubscriptionType = membershipSubscriptionType.getMembershipSubscriptionType();
        this.price = membershipSubscriptionType.getPrice();
        this.discountRate = membershipSubscriptionType.getDiscountRate();
        this.discountedPrice = membershipSubscriptionType.getPrice().multiply(BigDecimal.valueOf((100 - membershipSubscriptionType.getDiscountRate()) / 100.0));
    }
}
