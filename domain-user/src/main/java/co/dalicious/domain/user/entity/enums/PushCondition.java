package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PushCondition {
    ;

    private final String condition;
    private final Integer code;

    PushCondition(String condition, Integer code) {
        this.condition = condition;
        this.code = code;
    }

    public static PushCondition ofCode(Integer code) {
        return Arrays.stream(PushCondition.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
