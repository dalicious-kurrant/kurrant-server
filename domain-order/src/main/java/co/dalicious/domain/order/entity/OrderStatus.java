package co.dalicious.domain.order.entity;

import lombok.Getter;
import org.hibernate.boot.model.naming.IllegalIdentifierException;

import java.util.Arrays;

@Getter
public enum OrderStatus {
    COMPLETED("완료", 1L),
    PENDING_PAYMENT("결제 대기중", 2L),
    FAILED("주문 실패", 3L),
    PROCESSING("처리중", 4L),
    ON_HOLD("보류중", 5L),
    CANCELED("취소", 6L),
    MANUAL_REFUNDED("수동 환불", 7L),
    AUTO_REFUND("자동 환불", 8L);

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
