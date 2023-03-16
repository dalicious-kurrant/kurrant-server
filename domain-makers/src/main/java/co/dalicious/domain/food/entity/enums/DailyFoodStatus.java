package co.dalicious.domain.food.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DailyFoodStatus {

    WAITING_SALE("판매대기", 0),
    SALES("판매중", 1),
    SOLD_OUT("품절", 2),
    NOT_ALLOW_CANCEL("취소불가품", 3),
    STOP_SALE("판매중지", 4),
    WAITING_REGISTER("등록대기", 5),
    PASS_LAST_ORDER_TIME("주문마감", 6);

    private final String status;
    private final Integer code;

    DailyFoodStatus(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

    public static DailyFoodStatus ofCode(Integer code) {
        return Arrays.stream(DailyFoodStatus.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 타입입니다."));
    }

}
