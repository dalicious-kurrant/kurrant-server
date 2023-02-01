package co.dalicious.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "결제 부분 취소 요청 DTO")
public class PaymentCancelRequestDto {

    @Schema(description = "취소할 카드ID")
    private BigInteger cardId;
    @Schema(description = "결제 부분 취소할 OrderId")
    private BigInteger orderItemId;
    @Schema(description = "결제 취소 사유")
    private String cancelReason;
    @Schema(description = "부분 취소할 금액(값이없으면 전액취소)")
    private Integer cancelAmount;


}