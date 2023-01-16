package co.dalicious.domain.order.entity.enums;

import lombok.Getter;
import org.hibernate.boot.model.naming.IllegalIdentifierException;

import java.util.Arrays;

@Getter
public enum OrderStatus {
    INQUIRY("문의중", 1L),
    PROCESSING("처리중", 2L),
    PENDING_PAYMENT("결제대기중", 3L),
    FAILED("주문실패", 4L),
    COMPLETED("결제완료", 5L),
    WAIT_DELIVERY("배송대기", 6L),
    CANCELED("취소", 6L),
    DELIVERING("배송중", 7L),
    DELIVERED("수령/진행완료", 8L),
    MANUAL_REFUNDED("수동 환불", 9L),
    AUTO_REFUND("자동 환불", 10L);

    private final String orderStatus;
    private final Long code;

    OrderStatus(String orderStatus, Long code) {
        this.orderStatus = orderStatus;
        this.code = code;
    }

    public static OrderStatus ofCode(Long code) {
        return Arrays.stream(OrderStatus.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow( () -> new IllegalIdentifierException("존재하지 않은 주문 상태 타입입니다."));
    }
}
