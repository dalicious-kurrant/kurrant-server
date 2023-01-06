package co.dalicious.domain.order.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderType {
    DAILYFOOD("정기 식사 결제", 1L),
    PRODUCT("마켓 상품 결제", 2L),
    MEMBERSHIP("멤버십 결제", 3L);


    private final String orderType;
    private final Long code;

    OrderType(String orderType, Long code) {
        this.orderType = orderType;
        this.code = code;
    }

    public static OrderType ofCode(Long code) {
        return Arrays.stream(OrderType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제 타입입니다,"));
    }
}
