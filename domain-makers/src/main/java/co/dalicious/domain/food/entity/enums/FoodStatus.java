package co.dalicious.domain.food.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FoodStatus {
    WAIT_SALES("판매대기", 0),
    SALES("판매중", 1),
    NOT_ON_SALES("판매중지", 2);

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

    public static FoodStatus ofString(String status) {
        return Arrays.stream(FoodStatus.values())
                .filter(v -> v.getStatus().equals(status))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 타입입니다."));
    }

}
