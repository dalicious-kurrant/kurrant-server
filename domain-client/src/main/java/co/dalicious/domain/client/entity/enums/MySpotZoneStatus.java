package co.dalicious.domain.client.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MySpotZoneStatus {
    WAITE("오픈 대기", 0),
    OPEN("오픈", 1),
    CLOSE("정지", 2);
    private final String status;
    private final Integer code;

    MySpotZoneStatus(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

    public static MySpotZoneStatus ofCode(Integer dbData) {
        return Arrays.stream(MySpotZoneStatus.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태입니다."));
    }

}
