package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PushCondition {

    /*식단*/
    NEW_DAILYFOOD("새로운 식단이 등록되었을 때",1),
    LAST_ORDER_BY_DAILYFOOD("주문 마감이 임박했을 때", 2),

    /*배송*/
    DELIVERED_ORDER_ITEM("상품이 도착했을 때", 1001),

    /*리뷰*/
    REVIEW_DEADLINE("리뷰 작성 마감 하루 전", 2001),
    REVIEW_GET_COMMENT("리뷰에 댓글이 달렸을 때", 2002),

    /*공지 및 이벤트*/
    NEW_NOTICE("새로운 혜택 및 소식", 3001)
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
