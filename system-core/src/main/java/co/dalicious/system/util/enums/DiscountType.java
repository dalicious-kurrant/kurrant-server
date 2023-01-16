package co.dalicious.system.util.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DiscountType {
    MEMBERSHIP_DISCOUNT("멤버십 할인", 1),
    MAKERS_DISCOUNT("메이커스 할인", 2),
    PERIOD_DISCOUNT("기간 할인", 3),
    YEAR_DESCRIPTION_DISCOUNT("연간 구독 할인", 4);

    private final String discountType;
    private final Integer code;

    DiscountType(String discountType, Integer code) {
        this.discountType = discountType;
        this.code = code;
    }

    public static DiningType ofCode(Integer code) {
        return Arrays.stream(DiningType.values()).filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 할인 타입이 존재하지 않습니다."));
    }
}
