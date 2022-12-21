package co.dalicious.domain.user.entity;

import lombok.Getter;
import org.hibernate.boot.model.naming.IllegalIdentifierException;

import java.util.Arrays;

@Getter
public enum PaymentType {
    CREDIT_CARD("카드결제", 1),
    BANK_TRANSFER("계좌이체", 2);

    private final String paymentType;
    private final Integer code;

    PaymentType(String paymentType, Integer code) {
        this.paymentType = paymentType;
        this.code = code;
    }

    public static PaymentType ofCode(Integer dbData) {
       return Arrays.stream(PaymentType.values())
               .filter(v -> v.getCode().equals(dbData))
               .findAny()
               .orElseThrow(() -> new IllegalIdentifierException("존재하지 않는 결제 타입입니다."));
    }
}
