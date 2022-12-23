package co.dalicious.domain.user.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MembershipSubscriptionType {
    MONTH("월간구독", 12000.0, 0,1L),
    YEAR("연간구독",144000.0, 20, 2L);

    private final String membershipSubscriptionType;
    private final double price;
    private final int discountRate;
    private final Long code;

    MembershipSubscriptionType(String membershipSubscriptionType, double price, int discountRate, Long code) {
        this.membershipSubscriptionType = membershipSubscriptionType;
        this.price = price;
        this.discountRate = discountRate;
        this.code = code;
    }

    public static MembershipSubscriptionType ofCode(Long code) {
        return Arrays.stream(MembershipSubscriptionType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버십 타입입니다."));
    }

    public double getDiscountedPrice() {
        return this.price * (100 - this.discountRate) * 0.01;
    }

}
