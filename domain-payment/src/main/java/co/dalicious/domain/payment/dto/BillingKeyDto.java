package co.dalicious.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "나이스 빌링키 발급을 위한 요청 DTO")
public class BillingKeyDto {
    private String payNumber;
    private String email;
    private String password;
    private String corporationCode;
    private String cardNumber;
    private String cardType;
    private String cardPassword;
    private String expirationYear;
    private String expirationMonth;
    private String identityNumber;
    @Schema(description = "디폴트타입(0:아무것도아님, 1:기본결제카드, 2:멤버십 기본 결제 카드)")
    private Integer defaultType;

}
