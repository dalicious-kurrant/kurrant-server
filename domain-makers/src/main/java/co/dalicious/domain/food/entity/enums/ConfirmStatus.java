package co.dalicious.domain.food.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ConfirmStatus {

    PAUSE("임시저장",0),
    REQUEST("요청",1),
    COMPLETE("완료", 2);

    private final String confirmStatus;
    private final Integer code;


    ConfirmStatus(String status, Integer code) {
        this.confirmStatus = status;
        this.code = code;
    }

    public static ConfirmStatus ofCode(Integer code) {
        return Arrays.stream(ConfirmStatus.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태입니다."));
    }
}
