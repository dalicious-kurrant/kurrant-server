package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PointCondition {
    /* event */;

    private final String condition;
    private final Integer code;

    PointCondition(String condition, Integer code) {
        this.condition = condition;
        this.code = code;
    }

    public static PointCondition ofCode(Integer code) {
        return Arrays.stream(PointCondition.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static PointCondition ofValue(String value){
        return Arrays.stream(PointCondition.values())
                .filter(v -> v.getCondition().equals(value))
                .findAny()
                .orElse(null);
    }

}
