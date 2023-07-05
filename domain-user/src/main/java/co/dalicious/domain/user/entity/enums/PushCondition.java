package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum PushCondition {

    /*식단*/
    NEW_DAILYFOOD("식단추가","새로운 식단이 등록되었을 때",1),
    LAST_ORDER_BY_DAILYFOOD("주문마감","주문 마감이 임박했을 때", 2),

    /*배송*/
    DELIVERED_ORDER_ITEM("배송완료","상품이 도착했을 때", 1001),

    /*리뷰*/
    REVIEW_DEADLINE("리뷰작성","리뷰 작성 마감 하루 전", 2001),
    REVIEW_GET_COMMENT("리뷰댓글","리뷰에 댓글이 달렸을 때", 2002),

    /*공지 및 이벤트*/
    NEW_NOTICE("공지사항","새로운 혜택 및 소식", 3001),

    /*스팟*/
    NEW_SPOT("신청 스팟 생성", "신청한 스팟 생성 때", 4001), // 프라이빗스팟
    NEW_SPOT_2("신청 스팟 생성", "신청한 스팟 생성 때", 4002) // 마이스팟, 공유스팟
    ;
    private final String title;
    private final String condition;
    private final Integer code;

    PushCondition(String title, String condition, Integer code) {
        this.title = title;
        this.condition = condition;
        this.code = code;
    }

    public static PushCondition ofCode(Integer code) {
        return Arrays.stream(PushCondition.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static List<PushCondition> getCustomMessageCondition() {
        return Arrays.asList(
                NEW_DAILYFOOD,
                DELIVERED_ORDER_ITEM,
                LAST_ORDER_BY_DAILYFOOD,
                NEW_SPOT,
                NEW_SPOT_2
        );
    }

    public static PushCondition ofCondition(String message) {
        return Arrays.stream(PushCondition.values())
                .filter(v -> v.getCondition().equals(message))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static List<PushCondition> getBatchAlarmCondition() {
        return List.of(
                REVIEW_DEADLINE,
                LAST_ORDER_BY_DAILYFOOD,
                NEW_SPOT,
                NEW_SPOT_2
        );
    }

    public static List<PushCondition> getNoShowCondition() {
        return List.of(NEW_SPOT_2);
    }
}
