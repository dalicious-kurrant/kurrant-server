package co.dalicious.domain.order.entity.enums;

import co.dalicious.domain.order.entity.Order;
import lombok.Getter;
import org.hibernate.boot.model.naming.IllegalIdentifierException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum OrderStatus {
    INQUIRY("문의중", 1L),
    PROCESSING("처리중", 2L),
    PENDING_PAYMENT("결제대기중", 3L),
    FAILED("주문실패", 4L),
    COMPLETED("결제완료", 5L),
    WAIT_DELIVERY("배송대기", 6L),
    CANCELED("취소", 7L),
    WAITING_CANCEL("취소대기", 8L),
    DELIVERING("배송중", 9L),
    DELIVERED("배송완료", 10L),
    RECEIPT_COMPLETE("수령완료", 11L),
    MANUAL_REFUNDED("수동 환불", 12L),
    AUTO_REFUND("자동 환불", 13L);

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

    public static List<OrderStatus> completePayment() {
        List<OrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(OrderStatus.COMPLETED);
        orderStatuses.add(OrderStatus.WAIT_DELIVERY);
        orderStatuses.add(OrderStatus.DELIVERING);
        orderStatuses.add(OrderStatus.DELIVERED);
        orderStatuses.add(OrderStatus.RECEIPT_COMPLETE);
        return orderStatuses;
    }
}
