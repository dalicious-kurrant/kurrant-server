package co.dalicious.domain.client.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SpotStatus {
    INACTIVE("비활성", 0),
    ACTIVE("활성", 1);

    private final String spotStatus;
    private final Integer code;

    SpotStatus(String spotStatus, Integer code) {
        this.spotStatus = spotStatus;
        this.code = code;
    }

    public static SpotStatus ofCode(Integer dbData) {
        return Arrays.stream(SpotStatus.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스팟 상태입니다."));
    }


}
