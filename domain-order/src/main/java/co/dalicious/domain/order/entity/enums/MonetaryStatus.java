package co.dalicious.domain.order.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MonetaryStatus {
    DEDUCTION("차감", 1),
    REFUND("환불금 지급", 2);

    private final String monetaryStatus;
    private final Integer code;

    MonetaryStatus(String monetaryStatus, Integer code) {
        this.monetaryStatus = monetaryStatus;
        this.code = code;
    }

    public static MonetaryStatus ofCode(Integer code) {
        return Arrays.stream(MonetaryStatus.values()).filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
    }
}
