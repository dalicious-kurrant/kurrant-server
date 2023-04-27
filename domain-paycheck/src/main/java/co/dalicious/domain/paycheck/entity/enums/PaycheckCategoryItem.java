package co.dalicious.domain.paycheck.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaycheckCategoryItem {
    BREAKFAST("조식비", 1),
    LUNCH("중식비", 2),
    DINNER("석식비", 3),
    EXTRA_ORDER("추가 주문", 4),
    GARBAGE("쓰레기 수거", 5),
    HOT_STORAGE("온장고 사용", 6),
    SETTING("식사 세팅", 7)
    ;
    private final String paycheckCategoryItem;
    private final Integer code;

    PaycheckCategoryItem(String paycheckCategoryItem, Integer code) {
        this.paycheckCategoryItem = paycheckCategoryItem;
        this.code = code;
    }

    public static PaycheckCategoryItem ofCode(Integer dbData) {
        return Arrays.stream(PaycheckCategoryItem.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
