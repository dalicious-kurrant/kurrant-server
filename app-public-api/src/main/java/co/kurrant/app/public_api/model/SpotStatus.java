package co.kurrant.app.public_api.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SpotStatus {
    HAS_SPOT_AND_CLIENT("스팟과 그룹 모두 존재(홈화면)", 0),
    NO_SPOT_BUT_HAS_CLIENT("그룹은 존재하지만 스팟은 존재하지 않음(스팟 선택)", 1),
    NO_SPOT_AND_CLIENT("그룹과 스팟 모두 존재하지 않음(스팟 신청)", 2);

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
