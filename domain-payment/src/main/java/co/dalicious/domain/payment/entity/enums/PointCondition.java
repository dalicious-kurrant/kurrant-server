package co.dalicious.domain.payment.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PointCondition {
    /* review */
    PHOTO_REVIEW_RANGE_1("5000 ~ 7999", 1),
    PHOTO_REVIEW_RANGE_2("8000 ~ 12999", 2),
    PHOTO_REVIEW_RANGE_3("13000 ~ 19999", 3),
    PHOTO_REVIEW_RANGE_4("20000 ~ ", 4),
    CONTENT_REVIEW_RANGE_1("5000 ~ 7999", 5),
    CONTENT_REVIEW_RANGE_2("8000 ~ 12999", 6),
    CONTENT_REVIEW_RANGE_3("13000 ~ 19999", 7),
    CONTENT_REVIEW_RANGE_4("20000 ~ ", 8),


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
