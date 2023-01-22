package co.dalicious.domain.user.entity.enums;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
public enum MembershipSubscriptionType {
    MONTH("월간구독", BigDecimal.valueOf(10000.0), 0,1),
    YEAR("연간구독",BigDecimal.valueOf(120000.0), 20, 2);

    private final String membershipSubscriptionType;
    private final BigDecimal price;
    private final int discountRate;
    private final Integer code;

    MembershipSubscriptionType(String membershipSubscriptionType, BigDecimal price, int discountRate, Integer code) {
        this.membershipSubscriptionType = membershipSubscriptionType;
        this.price = price;
        this.discountRate = discountRate;
        this.code = code;
    }

    public static MembershipSubscriptionType ofCode(Integer code) {
        return Arrays.stream(MembershipSubscriptionType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버십 타입입니다."));
    }

}
