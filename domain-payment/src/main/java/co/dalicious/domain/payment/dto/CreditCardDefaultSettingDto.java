package co.dalicious.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "카드 세팅 Request DTO")
public class CreditCardDefaultSettingDto {
    @Schema(description = "결제할 카드 ID")
    private BigInteger cardId;
    @Schema(description = "변경할 디폴트 타입( 0: 아무것도 아닌 카드, 1:기본 결제카드, 2:멤버십 기본 결제 카드, 3: 둘 다")
    private Integer defaultType;
}
