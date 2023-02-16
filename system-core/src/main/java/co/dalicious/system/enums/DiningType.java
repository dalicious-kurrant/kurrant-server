package co.dalicious.system.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DiningType {
    MORNING("아침", 1),
    LUNCH("점심", 2),
    DINNER("저녁", 3);

    private final String diningType;
    private final Integer code;

    DiningType(String diningType, Integer code) {
        this.diningType = diningType;
        this.code = code;
    }

    public static DiningType ofCode(Integer code) {
        return Arrays.stream(DiningType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 타입입니다."));
    }

    public static DiningType ofString(String diningType) {
        return Arrays.stream(DiningType.values())
                .filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 타입입니다."));
    }
}
