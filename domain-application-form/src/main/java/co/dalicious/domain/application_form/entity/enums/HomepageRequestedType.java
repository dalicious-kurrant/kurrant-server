package co.dalicious.domain.application_form.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum HomepageRequestedType {
    CORPORATION("기업", 0),
    MAKERS("메이커스", 1),
    ;

    private final String type;
    private final Integer code;

    HomepageRequestedType(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    public static HomepageRequestedType ofCode(Integer code) {
        return Arrays.stream(HomepageRequestedType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 진행 타입입니다."));
    }
}
