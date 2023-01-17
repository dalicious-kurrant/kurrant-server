package co.dalicious.domain.board.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AlarmType {
   PROMOTION("프로모션", 0),
    EVENT("이벤트", 1),
    COUPON("쿠폰", 2),
    MEAL("정기식사", 3),
    NOTICE("공지", 4),
    SPOT_NOTICE("스팟공지", 5),
    ORDER_STATUS("주문상태", 6);

    private final String alarmType;
    private final Integer code;

    AlarmType(String alarmType, Integer code) {
        this.alarmType = alarmType;
        this.code = code;
    }

    public static AlarmType ofCode(Integer dbData) {
        return Arrays.stream(AlarmType.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알람 유형입니다."));

    }

}
