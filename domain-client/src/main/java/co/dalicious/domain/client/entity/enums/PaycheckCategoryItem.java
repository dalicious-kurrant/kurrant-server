package co.dalicious.domain.client.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaycheckCategoryItem {
    BREAKFAST("조식비", 1),
    LUNCH("중식비", 2),
    DINNER("석식비", 3),
    DELIVERY_FEE("배송비", 4),
    MEMBERSHIP("멤버십", 5),
    EXTRA_ORDER("추가 주문", 6),
    GARBAGE("쓰레기 수거", 7),
    HOT_STORAGE("온장고 사용", 8),
    SETTING("식사 세팅", 9)
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
