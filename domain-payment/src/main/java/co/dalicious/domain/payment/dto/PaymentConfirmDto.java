package co.dalicious.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "결제 승인 요청 DTO")
public class PaymentConfirmDto {
    @Schema(description = "토스 paymentKey")
    private String paymentKey;
    @Schema(description = "주문번호")
    private String orderId;
    @Schema(description = "결제할 가격")
    private Integer amount;
}
