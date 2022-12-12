package co.dalicious.domain.user.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MembershipSubscriptionType {
    MONTH("월간 구독", 1L),
    YEAR("연간 구독", 2L);

    private final String membershipSubscriptionType;
    private final Long code;

    MembershipSubscriptionType(String membershipSubscriptionType, Long code) {
        this.membershipSubscriptionType = membershipSubscriptionType;
        this.code = code;
    }
    public static MembershipSubscriptionType ofCode(Long code) {
        return Arrays.stream(MembershipSubscriptionType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버십 타입입니다."));
    }

}
