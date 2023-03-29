package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PointCondition {
    /* event - user */
    USER_ATTENDANCE("출석 체크", 1),

    /* event - order */
    ORDER_FIRST_ORDER("첫구매 이벤트",101);

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
