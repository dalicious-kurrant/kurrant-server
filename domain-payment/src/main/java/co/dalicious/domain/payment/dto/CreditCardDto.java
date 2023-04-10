package co.dalicious.domain.payment.dto;

import lombok.Getter;
import lombok.Setter;

public class CreditCardDto {
    @Getter
    @Setter
    public static class Response {
        private String cardNumber;
        private String cardCompany;
        private String customerKey;
        private String billingKey;
    }
}
