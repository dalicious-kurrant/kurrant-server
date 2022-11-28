package co.dalicious.system.util;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DiningType {
    NULL("선택안함", 0L),
    MORNING("아침", 1L),
    LUNCH("점심", 2L),
    DINNER("저녁", 3L);

    private final String diningType;
    private final Long code;

    DiningType(String diningType, Long code) {
        this.diningType = diningType;
        this.code = code;
    }

    public static DiningType ofCode(Long code) {
        return Arrays.stream(DiningType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 타입입니다."));
    }
}
