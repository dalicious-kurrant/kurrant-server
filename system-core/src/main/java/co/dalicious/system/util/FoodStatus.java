package co.dalicious.system.util;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FoodStatus {

    SALES_END("판매종료", 0),
    SALES("판매중", 1),
    REQUEST("일정요청", 2),
    APPROVAL("일정승인", 3),
    WAITING("등록대기", 4);

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
