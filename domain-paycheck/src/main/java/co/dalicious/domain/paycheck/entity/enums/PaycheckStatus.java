package co.dalicious.domain.paycheck.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaycheckStatus {
    REGISTER("정산 신청 완료", 0),
    WAITING_CONFIRM("거래명세서 확정 대기", 1),
    PAYMENT_COMPLETE("정산금 입금 완료", 2),
    ADD_REQUEST_COMPLETE("추가 요청 처리 완료", 3),
    TRANSACTION_CONFIRM("거래명세서 확정", 4);

    private final String paycheckStatus;
    private final Integer code;

    PaycheckStatus(String paycheckStatus, Integer code) {
        this.paycheckStatus = paycheckStatus;
        this.code = code;
    }

    public static PaycheckStatus ofCode(Integer dbData) {
        if(dbData == null) return null;
        return Arrays.stream(PaycheckStatus.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(null);
    }

    public static PaycheckStatus ofString(String paycheckStatus) {
        return Arrays.stream(PaycheckStatus.values())
                .filter(v -> v.getPaycheckStatus().equals(paycheckStatus))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

}
