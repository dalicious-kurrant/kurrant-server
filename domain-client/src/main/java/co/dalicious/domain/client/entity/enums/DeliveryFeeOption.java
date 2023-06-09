package co.dalicious.domain.client.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DeliveryFeeOption {
    PERSONAL("개인", 0),
    CORPORATION("기업", 1);
    private final String deliveryFeeOption;
    private final Integer code;

    DeliveryFeeOption(String deliveryFeeOption, Integer code) {
        this.deliveryFeeOption = deliveryFeeOption;
        this.code = code;
    }

    public static DeliveryFeeOption ofCode(Integer dbData) {
        return Arrays.stream(DeliveryFeeOption.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static DeliveryFeeOption ofString(String data) {
        return Arrays.stream(DeliveryFeeOption.values())
                .filter(v -> v.getDeliveryFeeOption().equals(data))
                .findAny()
                .orElse(null);
    }
}
