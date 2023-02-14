package co.dalicious.domain.user.entity.enums;

import lombok.Getter;
import org.hibernate.boot.model.naming.IllegalIdentifierException;

import java.util.Arrays;

@Getter
public enum PaymentType {
    CREDIT_CARD("카드결제", 1),
    SUPPORT_PRICE("기업 멤버십 지원금 사용", 2),
    BANK_TRANSFER("계좌이체", 3);

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
