package co.dalicious.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Schema(description = "결제 부분 취소 요청 DTO")
public class PaymentCancelResponseDto {

    @Schema(description = "주문 코드")
    private String orderCode;
    @Schema(description = "영수증 url")
    private String receiptUrl;
    @Schema(description = "부분 취소할 금액(값이없으면 전액취소)")
    private BigDecimal cancelAmount;

    @Builder
    public PaymentCancelResponseDto(String orderCode, String receiptUrl, BigDecimal cancelAmount) {
        this.orderCode = orderCode;
        this.receiptUrl = receiptUrl;
        this.cancelAmount = cancelAmount;
    }
}