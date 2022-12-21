package co.dalicious.domain.user.dto;

import co.dalicious.domain.user.entity.MembershipSubscriptionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "멤버십 구독 정보 응답 DTO")
@Getter
@NoArgsConstructor
public class MembershipSubscriptionTypeDto {
    private String membershipSubscriptionType;
    private double price;
    private int discountRate;
    private double discountedPrice;

    @Builder
    public MembershipSubscriptionTypeDto(MembershipSubscriptionType membershipSubscriptionType) {
        this.membershipSubscriptionType = membershipSubscriptionType.getMembershipSubscriptionType();
        this.price = membershipSubscriptionType.getPrice();
        this.discountRate = membershipSubscriptionType.getDiscountRate();
        this.discountedPrice = membershipSubscriptionType.getDiscountedPrice();
    }
}
