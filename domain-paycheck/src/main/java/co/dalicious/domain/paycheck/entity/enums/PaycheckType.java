package co.dalicious.domain.paycheck.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaycheckType {
    NO_MEMBERSHIP("비멤버십 후불", 1),
    POSTPAID_MEMBERSHIP("멤버십 후불", 2),
    PREPAID_MEMBERSHIP("멤버십 선불", 3),
    PREPAID_MEMBERSHIP_EXCEPTION("예외 멤버십 선불", 4),
    PREPAID_MEMBERSHIP_EXCEPTION_MEDTRONIC("예외 멤버십 메드트로닉", 4)
    ;
    private final String paycheckType;
    private final Integer code;

    PaycheckType(String paycheckType, Integer code) {
        this.paycheckType = paycheckType;
        this.code = code;
    }

    public static PaycheckType ofCode(Integer data) {
        return Arrays.stream(PaycheckType.values())
                .filter(v -> v.getCode().equals(data))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
