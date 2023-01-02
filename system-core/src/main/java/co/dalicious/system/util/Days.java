package co.dalicious.system.util;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Days {
    MON("월", 0),
    TUE("화", 1),
    WED("수", 2),
    THR("목", 3),
    FRI("금", 4),
    SAT("토", 5),
    SUN("일", 6);

    private final String days;
    private final Integer code;

    Days(String days, Integer code) {
        this.days = days;
        this.code = code;
    }

    public static Days ofCode(Integer dbData) {
        return Arrays.stream(Days.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요일입니다."));
    }
}
