package co.dalicious.domain.user.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GourmetType {
    NULL("정보없음", 0),
    SULLUNGTANG_GOURMET("설렁탕 미식가", 1);

    private final String gourmetType;
    private final Integer code;

    GourmetType(String gourmetType, Integer code) {
        this.gourmetType = gourmetType;
        this.code = code;
    }

    public static GourmetType ofCode(Integer code) {
        return Arrays.stream(GourmetType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미식가 타입입니다."));
    }
}
