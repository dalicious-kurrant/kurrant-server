package co.dalicious.domain.user.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GourmetType {
    NULL("정보 없음", 0L),
    SULLUNGTANG_GOURMET("설렁탕 미식가", 1L);

    private final String gourmetType;
    private final Long code;

    GourmetType(String gourmetType, Long code) {
        this.gourmetType = gourmetType;
        this.code = code;
    }

    public static GourmetType ofCode(Long code) {
        return Arrays.stream(GourmetType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미식가 타입입니다."));
    }
}
