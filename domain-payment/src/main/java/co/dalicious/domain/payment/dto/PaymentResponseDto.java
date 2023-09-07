package co.dalicious.domain.payment.dto;

import co.dalicious.domain.payment.entity.enums.PaymentCompany;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentResponseDto {
    private String receipt;
    private String transactionCode;
    private PaymentCompany paymentCompany;

    @Builder
    public PaymentResponseDto(String receipt, String transactionCode, PaymentCompany paymentCompany) {
        this.receipt = receipt;
        this.transactionCode = transactionCode;
        this.paymentCompany = paymentCompany;
    }
}
