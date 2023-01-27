package co.dalicious.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "결제 카드를 삭제 요청 DTO")
public class DeleteCreditCardDto {
    @Schema(description = "삭제할 카드 ID")
    private BigInteger cardId;
}
