package co.dalicious.domain.delivery.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DeliveryStatus {
    WAIT_DELIVERY("배송대기", 0),
    DELIVERED("배송완료", 1),
    REQUEST_DELIVERED("배송완료 요청", 2);


    private final String deliveryStatus;
    private final Integer code;

    DeliveryStatus(String deliveryStatus, Integer code) {
        this.deliveryStatus = deliveryStatus;
        this.code = code;
    }

    public static DeliveryStatus ofCode(Integer dbData) {
        return Arrays.stream(DeliveryStatus.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
