package co.dalicious.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "빌링키 발급 요청 Dto")
public class OrderCreateBillingKeySecondReqDto {
    private String cardNumber;
    private String expirationYear;
    private String expirationMonth;
    private String cardPassword;
    private String identityNumber;
    @Schema(description = "디폴트타입(0:아무것도아님, 1:기본결제카드, 2:멤버십 기본 결제 카드)")
    private Integer defaultType;
    private String payNumber;

}
