package co.dalicious.domain.food.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ScheduleStatus {

    REQUEST("요청", 0),
    APPROVAL("승인", 1),
    REJECTED("거절", 2),
    WAITING("대기", 3),
    COMPLETE("완료", 4);



    private final String status;
    private final Integer code;

    ScheduleStatus(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

    public static ScheduleStatus ofCode(Integer code) {
        return Arrays.stream(ScheduleStatus.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태입니다."));
    }
}
