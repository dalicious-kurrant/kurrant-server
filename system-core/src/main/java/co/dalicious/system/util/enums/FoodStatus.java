package co.dalicious.system.util.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FoodStatus {

    SOLD_OUT("판매종료", 0),
    SALES("판매중", 1),
    PASS_LAST_ORDER_TIME("주문마감", 2),
    REQUEST("일정요청", 3),
    APPROVAL("일정승인", 4),
    WAITING("등록대기", 5);

    private final String status;
    private final Integer code;

    FoodStatus(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

    public static FoodStatus ofCode(Integer code) {
        return Arrays.stream(FoodStatus.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 타입입니다."));
    }

}
