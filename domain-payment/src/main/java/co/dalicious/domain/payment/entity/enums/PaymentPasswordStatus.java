package co.dalicious.domain.payment.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentPasswordStatus {
    HAS_PAYMENT_PASSWORD("결제 비밀번호 존재", 1),
    NOT_HAVE_PAYMENT_PASSWORD_GENERAL("결제 비밀번호 부재", 2),
    NOT_HAVE_PAYMENT_PASSWORD_AND_HIDE_EMAIL("결제 비밀번호 부재 및 이메일 가리기", 3);
    private final String status;
    private final Integer code;

    PaymentPasswordStatus(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

    public static PaymentPasswordStatus ofCode(Integer data) {
        return Arrays.stream(PaymentPasswordStatus.values())
                .filter(v -> v.getCode().equals(data))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
